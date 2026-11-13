import time
import logging
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import or_
from sqlalchemy.orm import Session

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/sync", tags=["Sync"])

# گرفتن سشن بدون ایمپورت مستقیم در بالای فایل برای جلوگیری از Circular Import
def get_db():
    import main
    db = main.SessionLocal()
    try:
        yield db
    finally:
        db.close()

# مدل‌های ورودی پیام‌ها
class SyncEntryItem(BaseModel):
    id: str
    user_id: Optional[str] = None
    person_id: str
    body: str
    kind: str = "text"
    channel: str = "manual"
    occurred_at: int
    voice_path: Optional[str] = None
    media_path: Optional[str] = None
    member_id: Optional[str] = None
    target_recipient_id: Optional[str] = None

class SyncPayloadRequest(BaseModel):
    entries: List[SyncEntryItem]

# مدل‌های مخاطبین و گروه‌ها
class SyncPersonItem(BaseModel):
    id: str
    user_id: Optional[str] = None
    name: str
    phone: Optional[str] = None
    role: Optional[str] = None
    pinned: bool = False
    avatar: Optional[str] = None
    is_group: bool = False
    saved: bool = False
    created_at: int

class SyncPeoplePayload(BaseModel):
    people: List[SyncPersonItem]

# --- مدل‌های توکن‌ها ---
class TokenUsageSyncItem(BaseModel):
    user_id: str
    tokens: int
    occurred_at: int

class TokenUsageSyncPayload(BaseModel):
    usages: List[TokenUsageSyncItem]

# مدل‌های فیش‌های واریزی
class SyncSlipItem(BaseModel):
    id: str
    kind: str
    plan: str
    amount: int
    receipt: Optional[str] = None
    ref_code: Optional[str] = None
    created_at: int

class SyncSlipPayload(BaseModel):
    slips: List[SyncSlipItem]

# مدل‌های چت هوش مصنوعی
class SyncAiMessageItem(BaseModel):
    id: str
    user_id: Optional[str] = None
    person_id: Optional[str] = None
    role: str
    body: str
    image_path: Optional[str] = None
    created_at: int

class SyncAiPayload(BaseModel):
    messages: List[SyncAiMessageItem]

# پاسخ استاندارد به اندروید
class SyncResponse(BaseModel):
    status: str
    message: Optional[str] = None
    saved_count: int

# تابع کمکی ثبت رویداد سینک (PTS) برای هر کاربر
def record_sync_event(
    db: Session,
    user_id: str,
    entity_type: str,
    entity_id: str,
    payload: dict,
    action: str = "upsert"
) -> int:
    """درج یک رویداد سینک (PTS) برای کاربر. در صورت نبود user_id معتبر، صفر برمی‌گرداند."""
    import main
    clean_uid = str(user_id).strip() if user_id is not None else ""
    if not clean_uid or clean_uid == "None":
        return 0
    event = main.UserSyncEvent(
        user_id=clean_uid,
        entity_type=entity_type,
        entity_id=str(entity_id),
        action=action,
        payload=payload,
        created_at=int(time.time() * 1000)
    )
    db.add(event)
    return 1

# مدل‌های درخواست/پاسخ همگام‌سازی دلتا (getDifference)
class SyncDifferenceRequest(BaseModel):
    user_id: str
    pts: int = 0          # آخرین شماره ترتیبی کلاینت (0 یعنی دریافت اسنپ‌شات کامل)
    limit: int = 200      # سقف رویدادهای بازگردانده شده در هر درخواست
    offset: int = 0       # آفست صفحه‌بندی پیام‌های تیم در حالت اسنپ‌شات

class SyncDifferenceResponse(BaseModel):
    new_pts: int
    has_more: bool = False
    dialogs: List[dict] = []
    entries: List[dict] = []
    team_messages: List[dict] = []
    support_messages: List[dict] = []
    ai_config: Optional[dict] = None

# اندپوینت تجمیعی همگام‌سازی دلتا (مشابه updates.getDifference تلگرام)
@router.post("/difference", response_model=SyncDifferenceResponse)
def get_sync_difference(req: SyncDifferenceRequest, db: Session = Depends(get_db)):
    clean_uid = str(req.user_id).strip() if req.user_id is not None else ""
    if not clean_uid or clean_uid == "None":
        raise HTTPException(status_code=400, detail="user_id is required")

    logger.info(f"Sync difference requested: user_id={clean_uid} pts={req.pts} limit={req.limit}")

    from main import UserSyncEvent, Person, Entry, SupportMessage, TeamMessage, TeamMember, AiModel

    # ۱. پیدا کردن سقف رویدادهای کاربر
    latest_event = db.query(UserSyncEvent.id).filter(
        UserSyncEvent.user_id == clean_uid
    ).order_by(UserSyncEvent.id.desc()).first()
    max_pts = latest_event[0] if latest_event else 0

    # بررسی تنظیمات هوش مصنوعی (مدل فعال سراسری سیستم)
    active_model = db.query(AiModel).filter(AiModel.active == True).order_by(AiModel.created_at.desc()).first()
    ai_cfg = {
        "id": active_model.id,
        "provider": "openrouter",
        "model_name": active_model.name,
        "endpoint": active_model.endpoint,
        "api_key": active_model.api_key,
        "system_prompt": active_model.system_prompt or ""
    } if active_model else None

    # سناریو الف: کلاینت کش ندارد یا اولین بار است وارد می‌شود (pts == 0) -> اسنپ‌شات کامل
    if req.pts <= 0:
        people = db.query(Person).filter(Person.user_id == clean_uid).all()
        person_ids = [p.id for p in people]

        entries = []
        if person_ids:
            entries = db.query(Entry).filter(
                or_(Entry.user_id == clean_uid, Entry.person_id.in_(person_ids))
            ).order_by(Entry.occurred_at.asc()).all()
        else:
            entries = db.query(Entry).filter(Entry.user_id == clean_uid).all()

        support = db.query(SupportMessage).filter(SupportMessage.user_id == clean_uid).all()

        # دریافت پیام‌های تیم‌هایی که کاربر عضو آن‌هاست
        user_teams = db.query(TeamMember.team_id).filter(TeamMember.user_id == clean_uid).all()
        team_ids = [t[0] for t in user_teams]
        team_msgs = []
        snapshot_has_more = False
        if team_ids:
            # آخرین ۱۰۰ پیام تیم را با ترتیب نزولی می‌خوانیم، سپس برای ارسال به ترتیب زمانی صعودی معکوس می‌کنیم
            latest_team_msgs = db.query(TeamMessage).filter(
                TeamMessage.team_id.in_(team_ids),
                or_(TeamMessage.recipient_id.is_(None), TeamMessage.recipient_id == clean_uid, TeamMessage.sender_id == clean_uid)
            ).order_by(TeamMessage.created_at.desc()).offset(req.offset).limit(100).all()
            team_msgs = list(reversed(latest_team_msgs))
            # اگر سقف پر شده باشد یعنی صفحات/پیام‌های قدیمی‌تری هم وجود دارد
            snapshot_has_more = len(latest_team_msgs) >= 100

        logger.info(
            f"Sync difference snapshot: user_id={clean_uid} new_pts={max_pts} "
            f"dialogs={len(people)} entries={len(entries)} teams={len(team_ids)} "
            f"team_messages={len(team_msgs)} support={len(support)}"
        )

        return SyncDifferenceResponse(
            new_pts=max_pts,
            has_more=snapshot_has_more,
            dialogs=[{"id": p.id, "user_id": p.user_id, "name": p.name, "phone": p.phone, "role": p.role, "pinned": p.pinned, "created_at": p.created_at, "avatar": p.avatar, "is_group": p.is_group, "saved": p.saved} for p in people],
            entries=[{"id": e.id, "user_id": e.user_id, "person_id": e.person_id, "body": e.body, "kind": e.kind, "channel": e.channel, "occurred_at": e.occurred_at, "voice_path": e.voice_path, "media_path": e.media_path, "member_id": e.member_id, "target_recipient_id": e.target_recipient_id} for e in entries],
            team_messages=[{"id": m.id, "team_id": m.team_id, "sender_id": m.sender_id, "content": m.content, "recipient_id": m.recipient_id, "created_at": m.created_at} for m in team_msgs],
            support_messages=[{"id": s.id, "user_id": s.user_id, "from_admin": s.from_admin, "body": s.body, "kind": s.kind, "created_at": s.created_at} for s in support],
            ai_config=ai_cfg
        )

    # سناریو ب: همگام‌سازی دلتا رویدادمحور بر اساس PTS (pts > 0)
    events_query = db.query(UserSyncEvent).filter(
        UserSyncEvent.user_id == clean_uid,
        UserSyncEvent.id > req.pts
    ).order_by(UserSyncEvent.id.asc()).limit(req.limit + 1).all()

    has_more = len(events_query) > req.limit
    events = events_query[:req.limit]

    dialogs, entries, team_messages, support_messages = [], [], [], []
    current_pts = req.pts

    for ev in events:
        current_pts = max(current_pts, ev.id)
        p = ev.payload or {}
        if ev.entity_type == "dialog":
            dialogs.append(p)
        elif ev.entity_type == "entry":
            entries.append(p)
        elif ev.entity_type == "team_message":
            team_messages.append(p)
        elif ev.entity_type == "support":
            support_messages.append(p)

    logger.info(
        f"Sync difference delta: user_id={clean_uid} from_pts={req.pts} "
        f"new_pts={current_pts if events else req.pts} events={len(events)} has_more={has_more}"
    )

    return SyncDifferenceResponse(
        new_pts=current_pts if events else req.pts,
        has_more=has_more,
        dialogs=dialogs,
        entries=entries,
        team_messages=team_messages,
        support_messages=support_messages,
        ai_config=ai_cfg
    )

# ۱. اندپوینت ثبت پیام‌ها در سرور
@router.post("/entries", response_model=SyncResponse)
def sync_entries(payload: SyncPayloadRequest, db: Session = Depends(get_db)):
    import main
    Entry = main.Entry
    Person = main.Person
    try:
        saved = 0
        now_ms = int(time.time() * 1000)
        for item in payload.entries:
            # بررسی وجود مخاطب/گروه هدف (برای تعیین مالکیت و انتشار گروهی PTS)
            person = db.query(Person).filter(Person.id == item.person_id).first() if item.person_id else None
            # تعیین مالکیت: اول از payload، در غیر این صورت از مالک مخاطب مربوطه استخراج می‌شود
            owner_id = item.user_id
            if not owner_id and person:
                owner_id = person.user_id
            existing = db.query(Entry).filter(Entry.id == item.id).first()
            if existing:
                existing.body = item.body
                existing.kind = item.kind
                existing.channel = item.channel
                existing.occurred_at = item.occurred_at
                existing.voice_path = item.voice_path
                existing.media_path = item.media_path
                if item.member_id is not None:
                    existing.member_id = item.member_id
                existing.target_recipient_id = item.target_recipient_id
                if owner_id:
                    existing.user_id = owner_id
                existing.updated_at = now_ms
            else:
                db.add(Entry(
                    id=item.id,
                    user_id=owner_id,
                    person_id=item.person_id,
                    body=item.body,
                    kind=item.kind,
                    channel=item.channel,
                    occurred_at=item.occurred_at,
                    voice_path=item.voice_path,
                    media_path=item.media_path,
                    member_id=item.member_id,
                    target_recipient_id=item.target_recipient_id,
                    created_at=now_ms,
                    updated_at=now_ms,
                    pinned=False
                ))
            # ثبت رویداد سینک برای مالک پیام (PTS)
            event_payload = {
                "id": item.id,
                "user_id": owner_id,
                "person_id": item.person_id,
                "body": item.body,
                "kind": item.kind,
                "channel": item.channel,
                "occurred_at": item.occurred_at,
                "voice_path": item.voice_path,
                "media_path": item.media_path,
                "member_id": item.member_id,
                "target_recipient_id": item.target_recipient_id,
            }
            record_sync_event(db, owner_id, "entry", item.id, event_payload)
            # اگر پیام در یک گروه/تیم ارسال شده باشد، رویداد برای سایر اعضا هم صادر می‌شود
            # تا پیام از مسیر دلتای PTS به دستگاه آن‌ها برسد (مکمل حذف مسیر ارسال مستقیم تیم).
            if person is not None and person.is_group:
                if item.target_recipient_id:
                    group_recipients = [item.target_recipient_id]
                else:
                    group_recipients = [
                        m.user_id
                        for m in db.query(main.TeamMember).filter(main.TeamMember.team_id == item.person_id).all()
                    ]
                for uid in set(group_recipients):
                    if uid and uid != owner_id:
                        record_sync_event(db, uid, "entry", item.id, event_payload)
            saved += 1
        db.commit()
        return SyncResponse(status="success", message="پیام‌ها با موفقیت در سرور ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing entries: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۲. اندپوینت ثبت مخاطبین و گروه‌ها
@router.post("/people", response_model=SyncResponse)
def sync_people(payload: SyncPeoplePayload, db: Session = Depends(get_db)):
    import main
    Person = main.Person
    try:
        saved = 0
        for item in payload.people:
            existing = db.query(Person).filter(Person.id == item.id).first()
            if existing:
                existing.name = item.name
                existing.phone = item.phone
                existing.role = item.role
                existing.pinned = item.pinned
                existing.avatar = item.avatar
                existing.is_group = item.is_group
                existing.saved = item.saved
                existing.user_id = item.user_id if item.user_id else existing.user_id
            else:
                db.add(Person(
                    id=item.id,
                    user_id=item.user_id,
                    name=item.name,
                    phone=item.phone,
                    role=item.role,
                    pinned=item.pinned,
                    avatar=item.avatar,
                    is_group=item.is_group,
                    saved=item.saved,
                    created_at=item.created_at
                ))
            # ثبت رویداد سینک برای مالک مخاطب (PTS)
            owner = item.user_id or (existing.user_id if existing else None)
            record_sync_event(
                db,
                owner,
                "dialog",
                item.id,
                {
                    "id": item.id,
                    "user_id": owner,
                    "name": item.name,
                    "phone": item.phone,
                    "role": item.role,
                    "pinned": item.pinned,
                    "avatar": item.avatar,
                    "is_group": item.is_group,
                    "saved": item.saved,
                    "created_at": item.created_at,
                },
            )
            saved += 1
        db.commit()
        return SyncResponse(status="success", message="مخاطبین و گروه‌ها با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing people: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۳. اندپوینت ثبت فیش‌های واریزی
@router.post("/slips", response_model=SyncResponse)
def sync_slips(payload: SyncSlipPayload, db: Session = Depends(get_db)):
    import main
    Slip = main.Slip
    try:
        saved = 0
        for item in payload.slips:
            existing = db.query(Slip).filter(Slip.id == item.id).first()
            if not existing:
                db.add(Slip(
                    id=item.id,
                    kind=item.kind,
                    plan=item.plan,
                    amount=item.amount,
                    receipt=item.receipt,
                    ref_code=item.ref_code,
                    status="pending",
                    created_at=item.created_at
                ))
                saved += 1
        db.commit()
        return SyncResponse(status="success", message="فیش‌های واریزی با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing slips: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۴. اندپوینت ثبت چت‌های هوش مصنوعی
@router.post("/ai-messages", response_model=SyncResponse)
def sync_ai_messages(payload: SyncAiPayload, db: Session = Depends(get_db)):
    import main
    AiMessage = main.AiMessage
    try:
        saved = 0
        for item in payload.messages:
            existing = db.query(AiMessage).filter(AiMessage.id == item.id).first()
            if not existing:
                db.add(AiMessage(
                    id=item.id,
                    user_id=item.user_id,
                    person_id=item.person_id,
                    role=item.role,
                    body=item.body,
                    image_path=item.image_path,
                    created_at=item.created_at
                ))
                saved += 1
        db.commit()
        return SyncResponse(status="success", message="پیام‌های هوش مصنوعی با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing AI messages: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ───────────────────────────────────────────────────────
# ۵ بخش جدید: مدل‌های هوش مصنوعی، پشتیبانی، بنرها، اطلاعیه‌ها، خروجی‌ها
# ───────────────────────────────────────────────────────

# مدل‌های مربوط به AI Models
class SyncAiModelItem(BaseModel):
    id: str
    name: str
    endpoint: str
    model_id: str
    api_key: str
    input_cost: float
    output_cost: float
    system_prompt: Optional[str] = ""
    active: bool

class SyncAiModelsPayload(BaseModel):
    ai_models: List[SyncAiModelItem]

# مدل‌های مربوط به Support Messages
class SyncSupportMessageItem(BaseModel):
    id: str
    user_id: str
    from_admin: bool
    body: str
    kind: str
    path: Optional[str] = None
    read_admin: bool = False
    read_user: bool = False
    created_at: int

class SyncSupportMessagesPayload(BaseModel):
    support_messages: List[SyncSupportMessageItem]

# مدل‌های مربوط به Banners
class SyncBannerItem(BaseModel):
    id: str
    title: str
    body: str
    image: Optional[str] = None
    active: bool
    created_at: int

class SyncBannersPayload(BaseModel):
    banners: List[SyncBannerItem]

# مدل‌های مربوط به Announcements
class SyncAnnouncementItem(BaseModel):
    id: str
    title: str
    body: str
    created_at: int

class SyncAnnouncementsPayload(BaseModel):
    announcements: List[SyncAnnouncementItem]

# مدل‌های مربوط به Export Logs
class SyncExportLogItem(BaseModel):
    id: str
    user_id: str
    timestamp: int
    file_type: str
    target_name: str
    records_count: int

class SyncExportLogsPayload(BaseModel):
    export_logs: List[SyncExportLogItem]

# ۵. اندپوینت ثبت مدل‌های هوش مصنوعی
@router.post("/ai-models", response_model=SyncResponse)
def sync_ai_models(payload: SyncAiModelsPayload, db: Session = Depends(get_db)):
    import main
    AiModel = main.AiModel
    try:
        saved = 0
        now_ms = int(time.time() * 1000)
        for item in payload.ai_models:
            # اگر مدلی فعال ارسال شده، تمام مدل‌های دیگر را در سرور غیرفعال می‌کنیم (یکتاسازی)
            if item.active:
                db.query(AiModel).update({AiModel.active: False})
            
            existing = db.query(AiModel).filter(AiModel.id == item.id).first()
            if existing:
                existing.name = item.name
                existing.endpoint = item.endpoint
                existing.model_id = item.model_id
                existing.api_key = item.api_key
                existing.input_cost = item.input_cost
                existing.output_cost = item.output_cost
                existing.system_prompt = item.system_prompt
                existing.active = item.active
            else:
                db.add(AiModel(
                    id=item.id,
                    name=item.name,
                    endpoint=item.endpoint,
                    model_id=item.model_id,
                    api_key=item.api_key,
                    input_cost=item.input_cost,
                    output_cost=item.output_cost,
                    system_prompt=item.system_prompt,
                    active=item.active,
                    created_at=now_ms
                ))
            saved += 1
        db.commit()
        return SyncResponse(status="success", message="مدل‌های هوش مصنوعی با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing AI models: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۶. اندپوینت ثبت پیام‌های پشتیبانی
@router.post("/support-messages", response_model=SyncResponse)
def sync_support_messages(payload: SyncSupportMessagesPayload, db: Session = Depends(get_db)):
    import main
    SupportMessage = main.SupportMessage
    try:
        saved = 0
        for item in payload.support_messages:
            existing = db.query(SupportMessage).filter(SupportMessage.id == item.id).first()
            if existing:
                existing.body = item.body
                existing.kind = item.kind
                existing.path = item.path
                existing.read_admin = item.read_admin
                existing.read_user = item.read_user
            else:
                db.add(SupportMessage(
                    id=item.id,
                    user_id=item.user_id,
                    from_admin=item.from_admin,
                    body=item.body,
                    kind=item.kind,
                    path=item.path,
                    read_admin=item.read_admin,
                    read_user=item.read_user,
                    created_at=item.created_at
                ))
            # ثبت رویداد سینک برای مالک پیام پشتیبانی (PTS)
            record_sync_event(
                db,
                item.user_id,
                "support",
                item.id,
                {
                    "id": item.id,
                    "user_id": item.user_id,
                    "from_admin": item.from_admin,
                    "body": item.body,
                    "kind": item.kind,
                    "path": item.path,
                    "read_admin": item.read_admin,
                    "read_user": item.read_user,
                    "created_at": item.created_at,
                },
            )
            saved += 1
        db.commit()
        return SyncResponse(status="success", message="پیام‌های پشتیبانی با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing support messages: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۷. اندپوینت ثبت بنرها
@router.post("/banners", response_model=SyncResponse)
def sync_banners(payload: SyncBannersPayload, db: Session = Depends(get_db)):
    import main
    Banner = main.Banner
    try:
        saved = 0
        for item in payload.banners:
            existing = db.query(Banner).filter(Banner.id == item.id).first()
            if existing:
                existing.title = item.title
                existing.body = item.body
                existing.image = item.image
                existing.active = item.active
            else:
                db.add(Banner(
                    id=item.id,
                    title=item.title,
                    body=item.body,
                    image=item.image,
                    active=item.active,
                    created_at=item.created_at
                ))
            saved += 1
        db.commit()
        return SyncResponse(status="success", message="بنرها با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing banners: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۸. اندپوینت ثبت اطلاعیه‌ها
@router.post("/announcements", response_model=SyncResponse)
def sync_announcements(payload: SyncAnnouncementsPayload, db: Session = Depends(get_db)):
    import main
    Announcement = main.Announcement
    try:
        saved = 0
        for item in payload.announcements:
            existing = db.query(Announcement).filter(Announcement.id == item.id).first()
            if existing:
                existing.title = item.title
                existing.body = item.body
            else:
                db.add(Announcement(
                    id=item.id,
                    title=item.title,
                    body=item.body,
                    created_at=item.created_at
                ))
            saved += 1
        db.commit()
        return SyncResponse(status="success", message="اطلاعیه‌ها با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing announcements: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# ۹. اندپوینت ثبت لاگ‌های خروجی
@router.post("/export-logs", response_model=SyncResponse)
def sync_export_logs(payload: SyncExportLogsPayload, db: Session = Depends(get_db)):
    import main
    ExportLog = main.ExportLog
    try:
        saved = 0
        for item in payload.export_logs:
            existing = db.query(ExportLog).filter(ExportLog.id == item.id).first()
            if not existing:
                db.add(ExportLog(
                    id=item.id,
                    user_id=item.user_id,
                    timestamp=item.timestamp,
                    file_type=item.file_type,
                    target_name=item.target_name,
                    records_count=item.records_count
                ))
                saved += 1
        db.commit()
        return SyncResponse(status="success", message="لاگ‌های خروجی با موفقیت ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing export logs: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


# ۹. اندپوینت ثبت مصرف توکن‌ها
@router.post("/tokens", response_model=SyncResponse)
def sync_tokens(payload: TokenUsageSyncPayload, db: Session = Depends(get_db)):
    """
    دریافت لیست مصرف توکن‌های کاربران و بروزرسانی مقدار tokens_used در جدول users
    
    ورودی:
    {
        "usages": [
            {"user_id": "...", "tokens": 100, "occurred_at": 1698765432000},
            ...
        ]
    }
    """
    import main
    User = main.User
    try:
        saved = 0
        for item in payload.usages:
            user = db.query(User).filter(User.id == item.user_id).first()
            if user:
                # جمع مقدار توکن مصرفی
                user.tokens_used = (user.tokens_used or 0) + item.tokens
                saved += 1
            else:
                logger.warning(f"User {item.user_id} not found for token sync")
        
        db.commit()
        logger.info(f"Successfully synced {saved} token usages")
        return SyncResponse(status="success", message=f"مصرف {saved} کاربر با موفقیت ثبت شد", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing tokens: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

