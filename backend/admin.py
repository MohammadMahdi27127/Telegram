import hashlib
import logging
import secrets
import time
import uuid
from typing import List, Optional

from fastapi import APIRouter, Depends, Header, HTTPException
from pydantic import BaseModel, Field
from sqlalchemy import desc, func
from sqlalchemy.orm import Session

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/admin", tags=["Admin"])

ADMIN_TOKEN_SALT = "hamsohbat-admin-web-v1"


def get_db():
    from main import SessionLocal

    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def _admin_phone() -> str:
    from config import settings

    return (getattr(settings, "ADMIN_PHONE", None) or "09052893115").strip()


def make_admin_token(phone: str) -> str:
    raw = f"{ADMIN_TOKEN_SALT}:{phone.strip()}"
    return hashlib.sha256(raw.encode()).hexdigest()


def require_admin(
    x_admin_token: Optional[str] = Header(default=None, alias="X-Admin-Token"),
):
    expected = make_admin_token(_admin_phone())
    if not x_admin_token or secrets.compare_digest(x_admin_token, expected) is False:
        raise HTTPException(status_code=401, detail="دسترسی ادمین معتبر نیست")
    return True


# ─── Pydantic models ─────────────────────────────────────────────────────────


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
    ref_code: Optional[str] = None
    created_at: Optional[int] = None


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


# ─── Auth ────────────────────────────────────────────────────────────────────


@router.post("/login", response_model=AdminLoginResponse)
def admin_login(req: AdminLoginRequest, db: Session = Depends(get_db)):
    from main import User

    phone = req.phone.strip().replace(" ", "")
    if phone != _admin_phone():
        raise HTTPException(status_code=403, detail="فقط ادمین سیستم می‌تواند وارد شود")

    user = db.query(User).filter(User.phone == phone).first()
    if not user:
        raise HTTPException(status_code=401, detail="حساب ادمین یافت نشد")

    password_hash = hashlib.sha256(f"{user.salt}{req.password}".encode()).hexdigest()
    if password_hash != user.password_hash:
        from config import settings

        if req.password != getattr(settings, "ADMIN_PASSWORD", ""):
            raise HTTPException(status_code=401, detail="رمز عبور نادرست است")

    token = make_admin_token(phone)
    return AdminLoginResponse(
        status="ok",
        token=token,
        name=user.name,
        phone=user.phone,
        user_id=user.id,
    )


# ─── Users ───────────────────────────────────────────────────────────────────
# توجه: users-stats بدون require_admin می‌ماند تا APK اندروید نشکند.


@router.get("/users-stats", response_model=AdminStatsResponse)
def get_users_stats(
    query: Optional[str] = None,
    sort_by: str = "newest",
    plan: str = "all",
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

        if sort_by == "highest_usage":
            user_query = user_query.order_by(User.tokens_used.desc())
        elif sort_by == "lowest_usage":
            user_query = user_query.order_by(User.tokens_used.asc())
        else:
            user_query = user_query.order_by(User.created_at.desc())

        users = user_query.all()
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
            total_users=len(users),
            users=user_responses,
        )
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


# ─── Slips ────────────────────────────────────────────────────────────────────


@router.get("/slips", response_model=List[SlipResponse])
def list_slips(
    status: str = "all",
    _: bool = Depends(require_admin),
    db: Session = Depends(get_db),
):
    from main import Slip, User

    q = db.query(Slip).order_by(desc(Slip.created_at))
    if status != "all":
        q = q.filter(Slip.status == status)

    slips = q.limit(300).all()
    result: List[SlipResponse] = []
    for s in slips:
        u = db.query(User).filter(User.id == s.user_id).first() if s.user_id else None
        result.append(
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
                ref_code=s.ref_code,
                created_at=s.created_at,
            )
        )
    return result


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


# ─── Support ─────────────────────────────────────────────────────────────────


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
        logger.error(f"support threads error: {e}", exc_info=True)
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


# ─── AI Models ───────────────────────────────────────────────────────────────


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
