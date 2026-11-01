import time
import logging
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
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
    person_id: str
    body: str
    kind: str = "text"
    channel: str = "manual"
    occurred_at: int
    voice_path: Optional[str] = None
    media_path: Optional[str] = None
    target_recipient_id: Optional[str] = None

class SyncPayloadRequest(BaseModel):
    entries: List[SyncEntryItem]

# مدل‌های مخاطبین و گروه‌ها
class SyncPersonItem(BaseModel):
    id: str
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

# ۱. اندپوینت ثبت پیام‌ها در سرور
@router.post("/entries", response_model=SyncResponse)
def sync_entries(payload: SyncPayloadRequest, db: Session = Depends(get_db)):
    import main
    Entry = main.Entry
    try:
        saved = 0
        now_ms = int(time.time() * 1000)
        for item in payload.entries:
            existing = db.query(Entry).filter(Entry.id == item.id).first()
            if existing:
                existing.body = item.body
                existing.kind = item.kind
                existing.channel = item.channel
                existing.occurred_at = item.occurred_at
                existing.voice_path = item.voice_path
                existing.media_path = item.media_path
                existing.target_recipient_id = item.target_recipient_id
                existing.updated_at = now_ms
            else:
                db.add(Entry(
                    id=item.id,
                    user_id=None,
                    person_id=item.person_id,
                    body=item.body,
                    kind=item.kind,
                    channel=item.channel,
                    occurred_at=item.occurred_at,
                    voice_path=item.voice_path,
                    media_path=item.media_path,
                    target_recipient_id=item.target_recipient_id,
                    created_at=now_ms,
                    updated_at=now_ms,
                    pinned=False
                ))
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
            else:
                db.add(Person(
                    id=item.id,
                    name=item.name,
                    phone=item.phone,
                    role=item.role,
                    pinned=item.pinned,
                    avatar=item.avatar,
                    is_group=item.is_group,
                    saved=item.saved,
                    created_at=item.created_at
                ))
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
            existing = db.query(AiModel).filter(AiModel.id == item.id).first()
            if existing:
                existing.name = item.name
                existing.endpoint = item.endpoint
                existing.model_id = item.model_id
                existing.api_key = item.api_key
                existing.input_cost = item.input_cost
                existing.output_cost = item.output_cost
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

