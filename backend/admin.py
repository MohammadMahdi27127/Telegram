import hashlib
import logging
import mimetypes
import os
import secrets
import time
import uuid
from datetime import datetime, timezone
from pathlib import Path
from typing import List, Optional

from fastapi import APIRouter, Depends, File, Header, HTTPException, Query, UploadFile
from fastapi.responses import FileResponse
from pydantic import BaseModel, Field
from sqlalchemy import desc, func
from sqlalchemy.orm import Session

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/admin", tags=["Admin"])

ADMIN_TOKEN_SALT = "hamsohbat-admin-web-v1"
DOWNLOADS_DIR = Path(os.environ.get("DOWNLOADS_DIR", "downloads"))
RECEIPTS_DIR = DOWNLOADS_DIR / "receipts"
ALLOWED_RECEIPT_EXT = {".jpg", ".jpeg", ".png", ".webp", ".gif", ".pdf"}
MAX_RECEIPT_BYTES = 12 * 1024 * 1024

_PERSIAN_DIGITS = str.maketrans("۰۱۲۳۴۵۶۷۸۹٠١٢٣٤٥٦٧٨٩", "01234567890123456789")


def get_db():
    from main import SessionLocal

    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def _admin_phone() -> str:
    from config import settings

    return _normalize_phone(getattr(settings, "ADMIN_PHONE", None) or "09052893115")


def _admin_password() -> str:
    from config import settings

    return (getattr(settings, "ADMIN_PASSWORD", None) or "138727127").strip()


def _normalize_phone(phone: str) -> str:
    p = (phone or "").strip().replace(" ", "").replace("-", "")
    p = p.translate(_PERSIAN_DIGITS)
    if p.startswith("+98"):
        p = "0" + p[3:]
    elif p.startswith("98") and len(p) >= 12:
        p = "0" + p[2:]
    return p


def make_admin_token(phone: str) -> str:
    raw = f"{ADMIN_TOKEN_SALT}:{_normalize_phone(phone)}"
    return hashlib.sha256(raw.encode()).hexdigest()


def require_admin(
    x_admin_token: Optional[str] = Header(default=None, alias="X-Admin-Token"),
):
    expected = make_admin_token(_admin_phone())
    if not x_admin_token or secrets.compare_digest(x_admin_token, expected) is False:
        raise HTTPException(status_code=401, detail="دسترسی ادمین معتبر نیست")
    return True


def _parse_day_start(date_str: Optional[str]) -> Optional[int]:
    if not date_str:
        return None
    try:
        dt = datetime.strptime(date_str.strip(), "%Y-%m-%d").replace(tzinfo=timezone.utc)
        return int(dt.timestamp() * 1000)
    except ValueError:
        raise HTTPException(status_code=400, detail="فرمت تاریخ باید YYYY-MM-DD باشد")


def _parse_day_end(date_str: Optional[str]) -> Optional[int]:
    if not date_str:
        return None
    start = _parse_day_start(date_str)
    return start + 24 * 60 * 60 * 1000 - 1 if start is not None else None


def _paginate(total: int, page: int, page_size: int):
    page = max(1, page)
    page_size = min(max(1, page_size), 100)
    total_pages = max(1, (total + page_size - 1) // page_size) if total else 1
    offset = (page - 1) * page_size
    return page, page_size, total_pages, offset


def _hash_password(password: str, salt: str) -> str:
    return hashlib.sha256(f"{salt}{password}".encode()).hexdigest()


def _ensure_admin_user(db: Session, phone: str):
    """اگر کاربر ادمین نبود بساز؛ اگر بود و رمز env عوض شده، هش را به‌روز کن."""
    from main import User

    user = db.query(User).filter(User.phone == phone).first()
    pwd = _admin_password()
    if user:
        return user

    salt = secrets.token_hex(16)
    user = User(
        id=str(uuid.uuid4()),
        phone=phone,
        salt=salt,
        password_hash=_hash_password(pwd, salt),
        name="محمد مهدی",
        plan="admin",
        balance=1000000,
        suspended=False,
        banner_seen=True,
        created_at=int(time.time() * 1000),
        username="admin",
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    logger.info("Admin user auto-created during login: %s", phone)
    return user


class AdminLoginRequest(BaseModel):
    phone: str
    password: str


class AdminLoginResponse(BaseModel):
    status: str
    token: str
    name: Optional[str] = None
    phone: str
    user_id: Optional[str] = None


class UserStatsResponse(BaseModel):
    id: str
    phone: str
    name: Optional[str] = None
    username: Optional[str] = None
    plan: str
    balance: int = 0
    tokens_used: int = 0
    monthly_ai_usage_count: int = 0
    suspended: bool = False
    created_at: Optional[int] = None
    plan_expires_at: Optional[int] = None

    class Config:
        from_attributes = True


class AdminStatsResponse(BaseModel):
    total_tokens_used: int
    total_users: int
    page: int = 1
    page_size: int = 50
    total_pages: int = 1
    users: List[UserStatsResponse]


class SuspendRequest(BaseModel):
    suspended: bool = True


class SlipResponse(BaseModel):
    id: str
    user_id: Optional[str] = None
    user_name: Optional[str] = None
    user_phone: Optional[str] = None
    kind: Optional[str] = None
    plan: Optional[str] = None
    amount: int = 0
    status: str = "pending"
    receipt: Optional[str] = None
    receipt_url: Optional[str] = None
    ref_code: Optional[str] = None
    created_at: Optional[int] = None


class SlipListResponse(BaseModel):
    total: int
    page: int
    page_size: int
    total_pages: int
    items: List[SlipResponse]


class SlipActionRequest(BaseModel):
    action: str = Field(..., description="approve | reject")
    user_id: Optional[str] = None
    plan: Optional[str] = None
    duration_days: int = 30


class SupportThreadResponse(BaseModel):
    user_id: str
    user_name: Optional[str] = None
    user_phone: Optional[str] = None
    last_message: Optional[str] = None
    last_at: Optional[int] = None
    unread_admin: int = 0
    total_messages: int = 0


class SupportMessageResponse(BaseModel):
    id: str
    user_id: str
    from_admin: bool
    body: str
    kind: Optional[str] = "text"
    path: Optional[str] = None
    read_admin: bool = False
    read_user: bool = False
    created_at: Optional[int] = None


class SupportReplyRequest(BaseModel):
    body: str = Field(..., min_length=1)
    kind: str = "text"


class AiModelResponse(BaseModel):
    id: str
    name: Optional[str] = None
    endpoint: Optional[str] = None
    model_id: Optional[str] = None
    api_key: Optional[str] = None
    input_cost: float = 0
    output_cost: float = 0
    system_prompt: Optional[str] = ""
    active: bool = False
    created_at: Optional[int] = None


class AiModelUpsertRequest(BaseModel):
    id: Optional[str] = None
    name: str
    endpoint: str
    model_id: str
    api_key: str
    input_cost: float = 0
    output_cost: float = 0
    system_prompt: str = ""
    active: bool = False


class SetActiveModelRequest(BaseModel):
    model_id: str


class NotificationsResponse(BaseModel):
    pending_slips: int = 0
    unread_support: int = 0
    latest_slip_at: Optional[int] = None
    latest_support_at: Optional[int] = None


def _receipt_url(receipt: Optional[str]) -> Optional[str]:
    if not receipt:
        return None
    if receipt.startswith("http://") or receipt.startswith("https://") or receipt.startswith("/"):
        return receipt
    name = Path(receipt).name
    return f"/api/v1/admin/receipts/{name}"


@router.post("/login", response_model=AdminLoginResponse)
def admin_login(req: AdminLoginRequest, db: Session = Depends(get_db)):
    from main import User

    phone = _normalize_phone(req.phone)
    password = (req.password or "").strip()
    admin_phone = _admin_phone()
    env_password = _admin_password()

    if phone != admin_phone:
        raise HTTPException(status_code=403, detail="فقط شمارهٔ ادمین سیستم مجاز به ورود است")

    user = db.query(User).filter(User.phone == phone).first()

    # مسیر ۱: رمز محیطی ADMIN_PASSWORD همیشه معتبر است
    if password == env_password:
        if not user:
            user = _ensure_admin_user(db, phone)
        else:
            # هم‌تراز کردن هش با رمز فعلی env (اگر قبلاً فرق داشت)
            expected = _hash_password(password, user.salt or "")
            if expected != (user.password_hash or ""):
                salt = secrets.token_hex(16)
                user.salt = salt
                user.password_hash = _hash_password(password, salt)
                db.commit()
                db.refresh(user)
        token = make_admin_token(phone)
        return AdminLoginResponse(
            status="ok",
            token=token,
            name=user.name,
            phone=user.phone,
            user_id=user.id,
        )

    # مسیر ۲: بررسی هش ذخیره‌شده در دیتابیس
    if not user:
        raise HTTPException(
            status_code=401,
            detail="حساب ادمین یافت نشد. رمز پیش‌فرض را امتحان کنید یا seed_admin را اجرا کنید.",
        )

    expected_hash = _hash_password(password, user.salt or "")
    if expected_hash != (user.password_hash or ""):
        raise HTTPException(status_code=401, detail="رمز عبور نادرست است")

    token = make_admin_token(phone)
    return AdminLoginResponse(
        status="ok",
        token=token,
        name=user.name,
        phone=user.phone,
        user_id=user.id,
    )


@router.get("/notifications", response_model=NotificationsResponse)
def admin_notifications(
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import Slip, SupportMessage

    pending_slips = (
        db.query(func.count(Slip.id)).filter(Slip.status == "pending").scalar() or 0
    )
    unread_support = (
        db.query(func.count(SupportMessage.id))
        .filter(
            SupportMessage.from_admin.is_(False),
            SupportMessage.read_admin.is_(False),
        )
        .scalar()
        or 0
    )
    latest_slip = db.query(func.max(Slip.created_at)).filter(Slip.status == "pending").scalar()
    latest_support = (
        db.query(func.max(SupportMessage.created_at))
        .filter(SupportMessage.from_admin.is_(False))
        .scalar()
    )
    return NotificationsResponse(
        pending_slips=int(pending_slips),
        unread_support=int(unread_support),
        latest_slip_at=latest_slip,
        latest_support_at=latest_support,
    )


@router.get("/users-stats", response_model=AdminStatsResponse)
def get_users_stats(
    query: Optional[str] = None,
    sort_by: str = "newest",
    plan: str = "all",
    date_from: Optional[str] = None,
    date_to: Optional[str] = None,
    page: int = Query(1, ge=1),
    page_size: int = Query(50, ge=1, le=100),
    db: Session = Depends(get_db),
):
    try:
        from main import User

        user_query = db.query(User)

        if plan != "all":
            user_query = user_query.filter(User.plan == plan)

        if query:
            search_pattern = f"%{query}%"
            user_query = user_query.filter(
                User.name.ilike(search_pattern)
                | User.phone.ilike(search_pattern)
                | User.id.ilike(search_pattern)
                | User.username.ilike(search_pattern)
            )

        ts_from = _parse_day_start(date_from)
        ts_to = _parse_day_end(date_to)
        if ts_from is not None:
            user_query = user_query.filter(User.created_at >= ts_from)
        if ts_to is not None:
            user_query = user_query.filter(User.created_at <= ts_to)

        total_users = user_query.count()
        page, page_size, total_pages, offset = _paginate(total_users, page, page_size)

        if sort_by == "highest_usage":
            user_query = user_query.order_by(User.tokens_used.desc())
        elif sort_by == "lowest_usage":
            user_query = user_query.order_by(User.tokens_used.asc())
        else:
            user_query = user_query.order_by(User.created_at.desc())

        users = user_query.offset(offset).limit(page_size).all()
        total_tokens = db.query(func.coalesce(func.sum(User.tokens_used), 0)).scalar() or 0

        user_responses = [
            UserStatsResponse(
                id=u.id,
                phone=u.phone or "",
                name=u.name,
                username=getattr(u, "username", None),
                plan=u.plan or "free",
                balance=u.balance or 0,
                tokens_used=u.tokens_used or 0,
                monthly_ai_usage_count=getattr(u, "monthly_ai_usage_count", 0) or 0,
                suspended=bool(getattr(u, "suspended", False)),
                created_at=u.created_at,
                plan_expires_at=getattr(u, "plan_expires_at", None),
            )
            for u in users
        ]

        return AdminStatsResponse(
            total_tokens_used=int(total_tokens),
            total_users=total_users,
            page=page,
            page_size=page_size,
            total_pages=total_pages,
            users=user_responses,
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching admin stats: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/users/{user_id}/suspend")
def suspend_user(
    user_id: str,
    req: SuspendRequest,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import User

    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر یافت نشد")
    if user.phone == _admin_phone():
        raise HTTPException(status_code=400, detail="نمی‌توان ادمین را تعلیق کرد")

    user.suspended = bool(req.suspended)
    db.commit()
    return {"status": "ok", "user_id": user.id, "suspended": user.suspended}


@router.post("/users/{user_id}/plan")
def admin_set_plan(
    user_id: str,
    plan: str = "plus",
    duration_days: int = 30,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import User

    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر یافت نشد")

    allowed = {"free", "guest", "plus", "smart", "advanced", "basic", "admin"}
    plan = plan.strip().lower()
    if plan not in allowed:
        raise HTTPException(status_code=400, detail="پلن نامعتبر")

    user.plan = plan
    if plan == "free":
        user.plan_expires_at = None
    else:
        user.plan_expires_at = int(time.time() * 1000) + max(1, duration_days) * 24 * 60 * 60 * 1000

    db.commit()
    return {
        "status": "ok",
        "user_id": user.id,
        "plan": user.plan,
        "plan_expires_at": user.plan_expires_at,
    }


@router.get("/slips", response_model=SlipListResponse)
def list_slips(
    status: str = "all",
    date_from: Optional[str] = None,
    date_to: Optional[str] = None,
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import Slip, User

    q = db.query(Slip)
    if status != "all":
        q = q.filter(Slip.status == status)

    ts_from = _parse_day_start(date_from)
    ts_to = _parse_day_end(date_to)
    if ts_from is not None:
        q = q.filter(Slip.created_at >= ts_from)
    if ts_to is not None:
        q = q.filter(Slip.created_at <= ts_to)

    total = q.count()
    page, page_size, total_pages, offset = _paginate(total, page, page_size)
    slips = q.order_by(desc(Slip.created_at)).offset(offset).limit(page_size).all()

    items: List[SlipResponse] = []
    for s in slips:
        u = db.query(User).filter(User.id == s.user_id).first() if s.user_id else None
        items.append(
            SlipResponse(
                id=s.id,
                user_id=s.user_id,
                user_name=u.name if u else None,
                user_phone=u.phone if u else None,
                kind=s.kind,
                plan=s.plan,
                amount=s.amount or 0,
                status=s.status or "pending",
                receipt=s.receipt,
                receipt_url=_receipt_url(s.receipt),
                ref_code=s.ref_code,
                created_at=s.created_at,
            )
        )
    return SlipListResponse(
        total=total, page=page, page_size=page_size, total_pages=total_pages, items=items
    )


@router.post("/slips/{slip_id}/action")
def slip_action(
    slip_id: str,
    req: SlipActionRequest,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import Slip, User

    slip = db.query(Slip).filter(Slip.id == slip_id).first()
    if not slip:
        raise HTTPException(status_code=404, detail="فیش یافت نشد")

    action = req.action.strip().lower()
    if action not in ("approve", "reject"):
        raise HTTPException(status_code=400, detail="action باید approve یا reject باشد")

    if action == "reject":
        slip.status = "rejected"
        db.commit()
        return {"status": "ok", "slip_id": slip.id, "new_status": "rejected"}

    target_user_id = req.user_id or slip.user_id
    if not target_user_id:
        raise HTTPException(
            status_code=400,
            detail="user_id برای این فیش مشخص نیست؛ لطفاً user_id را در درخواست بفرستید",
        )

    user = db.query(User).filter(User.id == target_user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر مرتبط با فیش یافت نشد")

    plan = (req.plan or slip.plan or "plus").strip().lower()
    if plan in ("", "free", "guest"):
        plan = "plus"

    slip.user_id = target_user_id
    slip.status = "approved"
    user.plan = plan
    user.plan_expires_at = int(time.time() * 1000) + max(1, req.duration_days) * 24 * 60 * 60 * 1000

    db.commit()
    return {
        "status": "ok",
        "slip_id": slip.id,
        "new_status": "approved",
        "user_id": user.id,
        "plan": user.plan,
        "plan_expires_at": user.plan_expires_at,
    }


@router.post("/slips/{slip_id}/receipt")
async def upload_slip_receipt(
    slip_id: str,
    file: UploadFile = File(...),
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import Slip

    slip = db.query(Slip).filter(Slip.id == slip_id).first()
    if not slip:
        raise HTTPException(status_code=404, detail="فیش یافت نشد")

    original = file.filename or "receipt.bin"
    ext = Path(original).suffix.lower()
    if ext not in ALLOWED_RECEIPT_EXT:
        raise HTTPException(
            status_code=400,
            detail=f"فرمت مجاز: {', '.join(sorted(ALLOWED_RECEIPT_EXT))}",
        )

    content = await file.read()
    if len(content) > MAX_RECEIPT_BYTES:
        raise HTTPException(status_code=400, detail="حجم فایل بیش از ۱۲ مگابایت است")

    RECEIPTS_DIR.mkdir(parents=True, exist_ok=True)
    safe_name = f"{slip_id}_{int(time.time())}{ext}"
    dest = RECEIPTS_DIR / safe_name
    dest.write_bytes(content)

    relative = f"receipts/{safe_name}"
    slip.receipt = relative
    db.commit()

    return {
        "status": "ok",
        "slip_id": slip.id,
        "receipt": relative,
        "receipt_url": _receipt_url(relative),
    }


@router.get("/receipts/{filename}")
def get_receipt_file(
    filename: str,
    _: bool = Depends(require_admin),
):
    safe = Path(filename).name
    path = RECEIPTS_DIR / safe
    if not path.is_file():
        alt = DOWNLOADS_DIR / safe
        if alt.is_file():
            path = alt
        else:
            raise HTTPException(status_code=404, detail="فایل یافت نشد")

    media_type, _ = mimetypes.guess_type(str(path))
    return FileResponse(path, media_type=media_type or "application/octet-stream", filename=safe)


@router.get("/support/threads", response_model=List[SupportThreadResponse])
def list_support_threads(
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import SupportMessage, User

    try:
        user_ids = [r[0] for r in db.query(SupportMessage.user_id).distinct().all()]
        threads: List[SupportThreadResponse] = []
        users = {
            u.id: u for u in db.query(User).filter(User.id.in_(user_ids)).all()
        } if user_ids else {}

        for uid in user_ids:
            last_msg = (
                db.query(SupportMessage)
                .filter(SupportMessage.user_id == uid)
                .order_by(desc(SupportMessage.created_at))
                .first()
            )
            total = (
                db.query(func.count(SupportMessage.id))
                .filter(SupportMessage.user_id == uid)
                .scalar()
                or 0
            )
            unread = (
                db.query(func.count(SupportMessage.id))
                .filter(
                    SupportMessage.user_id == uid,
                    SupportMessage.from_admin.is_(False),
                    SupportMessage.read_admin.is_(False),
                )
                .scalar()
                or 0
            )
            u = users.get(uid)
            threads.append(
                SupportThreadResponse(
                    user_id=uid,
                    user_name=u.name if u else None,
                    user_phone=u.phone if u else None,
                    last_message=last_msg.body if last_msg else None,
                    last_at=last_msg.created_at if last_msg else None,
                    unread_admin=int(unread),
                    total_messages=int(total),
                )
            )

        threads.sort(key=lambda t: t.last_at or 0, reverse=True)
        return threads
    except Exception as e:
        logger.error(f"support threads error: {e}", exp_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/support/{user_id}/messages", response_model=List[SupportMessageResponse])
def get_support_messages(
    user_id: str,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import SupportMessage

    msgs = (
        db.query(SupportMessage)
        .filter(SupportMessage.user_id == user_id)
        .order_by(SupportMessage.created_at.asc())
        .limit(500)
        .all()
    )

    for m in msgs:
        if not m.from_admin and not m.read_admin:
            m.read_admin = True
    db.commit()

    return [
        SupportMessageResponse(
            id=m.id,
            user_id=m.user_id,
            from_admin=bool(m.from_admin),
            body=m.body or "",
            kind=m.kind or "text",
            path=m.path,
            read_admin=bool(m.read_admin),
            read_user=bool(m.read_user),
            created_at=m.created_at,
        )
        for m in msgs
    ]


@router.post("/support/{user_id}/reply", response_model=SupportMessageResponse)
def reply_support(
    user_id: str,
    req: SupportReplyRequest,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import SupportMessage, User
    from sync import record_sync_event

    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر یافت نشد")

    now = int(time.time() * 1000)
    msg_id = str(uuid.uuid4())
    msg = SupportMessage(
        id=msg_id,
        user_id=user_id,
        from_admin=True,
        body=req.body.strip(),
        kind=req.kind or "text",
        path=None,
        read_admin=True,
        read_user=False,
        created_at=now,
    )
    db.add(msg)

    payload = {
        "id": msg_id,
        "user_id": user_id,
        "from_admin": True,
        "body": msg.body,
        "kind": msg.kind,
        "path": None,
        "read_admin": True,
        "read_user": False,
        "created_at": now,
    }
    try:
        record_sync_event(db, user_id, "support", msg_id, payload)
    except Exception as e:
        logger.warning(f"PTS support event failed: {e}")

    db.commit()

    return SupportMessageResponse(
        id=msg.id,
        user_id=msg.user_id,
        from_admin=True,
        body=msg.body,
        kind=msg.kind,
        path=None,
        read_admin=True,
        read_user=False,
        created_at=msg.created_at,
    )


@router.get("/ai-models", response_model=List[AiModelResponse])
def list_ai_models(
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import AiModel

    models = db.query(AiModel).order_by(desc(AiModel.created_at)).all()
    return [
        AiModelResponse(
            id=m.id,
            name=m.name,
            endpoint=m.endpoint,
            model_id=m.model_id,
            api_key=m.api_key,
            input_cost=m.input_cost or 0,
            output_cost=m.output_cost or 0,
            system_prompt=m.system_prompt or "",
            active=bool(m.active),
            created_at=m.created_at,
        )
        for m in models
    ]


@router.post("/ai-models", response_model=AiModelResponse)
def upsert_ai_model(
    req: AiModelUpsertRequest,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import AiModel

    model_id = req.id or str(uuid.uuid4())
    existing = db.query(AiModel).filter(AiModel.id == model_id).first()

    if req.active:
        db.query(AiModel).update({AiModel.active: False})

    if existing:
        existing.name = req.name
        existing.endpoint = req.endpoint
        existing.model_id = req.model_id
        existing.api_key = req.api_key
        existing.input_cost = req.input_cost
        existing.output_cost = req.output_cost
        existing.system_prompt = req.system_prompt
        existing.active = req.active
        m = existing
    else:
        m = AiModel(
            id=model_id,
            name=req.name,
            endpoint=req.endpoint,
            model_id=req.model_id,
            api_key=req.api_key,
            input_cost=req.input_cost,
            output_cost=req.output_cost,
            system_prompt=req.system_prompt,
            active=req.active,
            created_at=int(time.time() * 1000),
        )
        db.add(m)

    db.commit()
    db.refresh(m)

    return AiModelResponse(
        id=m.id,
        name=m.name,
        endpoint=m.endpoint,
        model_id=m.model_id,
        api_key=m.api_key,
        input_cost=m.input_cost or 0,
        output_cost=m.output_cost or 0,
        system_prompt=m.system_prompt or "",
        active=bool(m.active),
        created_at=m.created_at,
    )


@router.post("/ai-models/set-active")
def set_active_ai_model(
    req: SetActiveModelRequest,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import AiModel

    model = db.query(AiModel).filter(AiModel.id == req.model_id).first()
    if not model:
        raise HTTPException(status_code=404, detail="مدل یافت نشد")

    db.query(AiModel).update({AiModel.active: False})
    model.active = True
    db.commit()

    return {"status": "ok", "active_model_id": model.id, "name": model.name}


@router.delete("/ai-models/{model_id}")
def delete_ai_model(
    model_id: str,
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import AiModel

    model = db.query(AiModel).filter(AiModel.id == model_id).first()
    if not model:
        raise HTTPException(status_code=404, detail="مدل یافت نشد")
    db.delete(model)
    db.commit()
    return {"status": "ok", "deleted": model_id}
