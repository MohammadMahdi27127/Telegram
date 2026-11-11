"""نقطهٔ ورود بک‌اند «مکالمات من» (هم‌صحبت).

نکتهٔ مهم: این ماژول به دو صورت قابل اجراست و importها باید هر دو حالت را پوشش دهند:
    1. اجرای کانتینر:  uvicorn main:app         (main به‌عنوان ماژول سطح‌بالا)
    2. اجرای پکیجی:    uvicorn backend.main:app (main به‌عنوان زیرماژول پکیج)
"""

import logging
import time
from contextlib import asynccontextmanager
from typing import List, Optional

from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from sqlalchemy import (
    BigInteger,
    Boolean,
    Column,
    Float,
    Integer,
    String,
    Text,
    create_engine,
    text,
)
from sqlalchemy.orm import declarative_base, sessionmaker

logger = logging.getLogger("hamsohbat.backend")
logging.basicConfig(level=logging.INFO)

# --- تنظیمات (سازگار با هر دو حالت اجرا) ---
try:  # اجرای کانتینری: uvicorn main:app
    from config import settings
except ImportError:  # اجرای پکیجی: uvicorn backend.main:app
    from .config import settings


def get_database_url(raw_url: Optional[str] = None) -> str:
    """نرمال‌سازی آدرس دیتابیس برای SQLAlchemy.

    Darkube/Railway/Heroku اغلب آدرس را با طرح ``postgres://`` می‌دهند که
    نسخه‌های جدید SQLAlchemy آن را نمی‌شناسند؛ همچنین برای اطمینان از استفادهٔ
    درایور ``psycopg2``، طرح را به ``postgresql+psycopg2://`` تبدیل می‌کنیم.
    """
    url = (raw_url or settings.DATABASE_URL or "").strip()
    if url.startswith("postgres://"):
        url = url.replace("postgres://", "postgresql+psycopg2://", 1)
    elif url.startswith("postgresql://"):
        url = url.replace("postgresql://", "postgresql+psycopg2://", 1)
    return url


DATABASE_URL = get_database_url()

# ``pool_pre_ping`` از خطاهای قطع اتصال موقت در PostgreSQL جلوگیری می‌کند.
engine = create_engine(DATABASE_URL, pool_pre_ping=True, future=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


# --- مدل‌های دیتابیس (همه از یک Base مشترک ارث‌بری می‌کنند) ---

class User(Base):
    __tablename__ = "users"
    id = Column(String, primary_key=True)
    phone = Column(String, unique=True, index=True)
    salt = Column(String)
    password_hash = Column(String)
    name = Column(String)
    plan = Column(String, default="free")
    balance = Column(Integer, default=0)
    plan_expires_at = Column(BigInteger)
    suspended = Column(Boolean, default=False)
    banner_seen = Column(Boolean, default=False)
    chat_bg = Column(String)
    chat_color = Column(String)
    avatar = Column(String)
    username = Column(String)
    created_at = Column(BigInteger)
    monthly_download_count = Column(Integer, default=0)
    monthly_ai_usage_count = Column(Integer, default=0)
    last_quota_reset_month = Column(String)
    tokens_used = Column(Integer, default=0)


class Person(Base):
    __tablename__ = "people"
    id = Column(String, primary_key=True)
    # مالک پرونده/گروه؛ ایندکس‌شده تا کوئری‌های واکشی بدون cast و Full Scan اجرا شوند.
    user_id = Column(String, index=True, nullable=True)
    name = Column(String)
    phone = Column(String)
    role = Column(String)
    pinned = Column(Boolean, default=False)
    created_at = Column(BigInteger)
    avatar = Column(String)
    is_group = Column(Boolean, default=False)
    saved = Column(Boolean, default=False)


class Entry(Base):
    __tablename__ = "entries"
    id = Column(String, primary_key=True)
    # مالک پیام و شناسهٔ چت؛ هر دو ایندکس‌شده برای واکشی سریع بدون cast.
    user_id = Column(String, index=True, nullable=True)
    person_id = Column(String, index=True, nullable=True)
    body = Column(Text)
    kind = Column(String)
    channel = Column(String)
    occurred_at = Column(BigInteger)
    created_at = Column(BigInteger)
    updated_at = Column(BigInteger)
    target_recipient_id = Column(String)
    pinned = Column(Boolean, default=False)
    voice_path = Column(String)
    media_path = Column(String)
    member_id = Column(String)


class Notice(Base):
    __tablename__ = "notices"
    id = Column(String, primary_key=True)
    user_id = Column(String, index=True)
    title = Column(String)
    body = Column(Text)
    read = Column(Boolean, default=False)
    created_at = Column(BigInteger)


class Banner(Base):
    __tablename__ = "banners"
    id = Column(String, primary_key=True)
    title = Column(String, nullable=False)
    body = Column(Text, nullable=False)
    image = Column(String)
    active = Column(Boolean, default=True)
    created_at = Column(BigInteger, nullable=False)


class AiModel(Base):
    __tablename__ = "ai_models"
    id = Column(String, primary_key=True)
    name = Column(String)
    endpoint = Column(String)
    model_id = Column(String)
    api_key = Column(String)
    input_cost = Column(Float)
    output_cost = Column(Float)
    active = Column(Boolean, default=False)
    created_at = Column(BigInteger)


class Announcement(Base):
    __tablename__ = "announcements"
    id = Column(String, primary_key=True)
    title = Column(String)
    body = Column(Text)
    created_at = Column(BigInteger)


class Slip(Base):
    __tablename__ = "slips"
    id = Column(String, primary_key=True)
    user_id = Column(String, index=True)
    kind = Column(String)
    plan = Column(String)
    amount = Column(Integer)
    status = Column(String, default="pending")
    receipt = Column(String)
    ref_code = Column(String)
    created_at = Column(BigInteger)


class Member(Base):
    __tablename__ = "members"
    id = Column(String, primary_key=True)
    person_id = Column(String, index=True)
    name = Column(String)
    created_at = Column(BigInteger)


class Wallpaper(Base):
    __tablename__ = "wallpapers"
    id = Column(String, primary_key=True)
    title = Column(String)
    category = Column(String)
    path = Column(String)
    created_at = Column(BigInteger)


class Outbox(Base):
    __tablename__ = "outbox"
    id = Column(String, primary_key=True)
    kind = Column(String)
    payload = Column(Text)
    created_at = Column(BigInteger)


class AiMessage(Base):
    __tablename__ = "ai_messages"
    id = Column(String, primary_key=True)
    user_id = Column(String, index=True)
    person_id = Column(String, index=True)
    role = Column(String)
    body = Column(Text)
    image_path = Column(String)
    created_at = Column(BigInteger)


class SupportMessage(Base):
    __tablename__ = "support_messages"
    id = Column(String, primary_key=True)
    user_id = Column(String, index=True)
    from_admin = Column(Boolean, default=False)
    body = Column(Text)
    kind = Column(String)
    path = Column(String)
    read_admin = Column(Boolean, default=False)
    read_user = Column(Boolean, default=False)
    created_at = Column(BigInteger)


class ExportLog(Base):
    __tablename__ = "export_logs"
    id = Column(String, primary_key=True)
    user_id = Column(String, index=True)
    timestamp = Column(BigInteger)
    file_type = Column(String)
    target_name = Column(String)
    records_count = Column(Integer)


# --- مدل‌های تیم (مورد استفاده در teams.py) ---

class Team(Base):
    __tablename__ = "teams"
    id = Column(String, primary_key=True)
    name = Column(String, nullable=False)
    description = Column(String)
    creator_id = Column(String, index=True, nullable=False)
    created_at = Column(BigInteger, nullable=False)


class TeamMember(Base):
    __tablename__ = "team_members"
    id = Column(String, primary_key=True)
    team_id = Column(String, index=True, nullable=False)
    user_id = Column(String, index=True, nullable=False)
    role = Column(String, default="member")
    joined_at = Column(BigInteger)


class TeamMessage(Base):
    __tablename__ = "team_messages"
    id = Column(String, primary_key=True)
    team_id = Column(String, index=True, nullable=False)
    sender_id = Column(String, index=True, nullable=False)
    content = Column(Text)
    recipient_id = Column(String)
    created_at = Column(BigInteger)


# ایندکس‌های ضروری برای واکشی سریع سینک (بدون cast و Full Table Scan).
# نام‌گذاری مطابق قرارداد SQLAlchemy (ix_...) است تا با create_all تکراری ساخته نشود.
_REQUIRED_INDEXES: tuple[str, ...] = (
    "CREATE INDEX IF NOT EXISTS ix_people_user_id ON people (user_id);",
    "CREATE INDEX IF NOT EXISTS ix_entries_user_id ON entries (user_id);",
    "CREATE INDEX IF NOT EXISTS ix_entries_person_id ON entries (person_id);",
)


def _ensure_indexes() -> None:
    """اجرای ایمن مایگریشن ایندکس‌ها روی جداول موجود (idempotent)."""
    with engine.begin() as conn:
        for statement in _REQUIRED_INDEXES:
            try:
                conn.execute(text(statement))
            except Exception as exc:  # noqa: BLE001 - نبود یک ایندکس کل استارت‌آپ را نباید بخواباند
                logger.warning("⚠️ ساخت ایندکس ناموفق بود: %s | %s", statement, exc)
    logger.info("✅ ایندکس‌های سینک بررسی/ایجاد شدند.")


def init_db(max_attempts: int = 5, delay_seconds: int = 3) -> bool:
    """ساخت جداول با تلاش مجدد؛ تا خطای موقت اتصال باعث CrashLoopBackOff نشود."""
    for attempt in range(1, max_attempts + 1):
        try:
            Base.metadata.create_all(bind=engine)
            _ensure_indexes()
            logger.info("✅ جداول دیتابیس با موفقیت ایجاد/تأیید شدند.")
            return True
        except Exception as exc:  # noqa: BLE001 - می‌خواهیم هر خطای اتصال را مهار کنیم
            logger.warning(
                "⚠️ تلاش %s/%s برای اتصال به دیتابیس ناموفق بود: %s",
                attempt,
                max_attempts,
                exc,
            )
            if attempt < max_attempts:
                time.sleep(delay_seconds)
    logger.error(
        "❌ اتصال به دیتابیس برقرار نشد؛ سرویس در حالت degraded بالا می‌آید و "
        "اندپوینت /api/health وضعیت را گزارش می‌کند."
    )
    return False


@asynccontextmanager
async def lifespan(_: FastAPI):
    # استارت‌آپ: تلاش برای ساخت جداول بدون کرش کردن کانتینر
    init_db()
    yield
    # خاموشی: آزادسازی منابع موتور دیتابیس
    engine.dispose()


app = FastAPI(title="Hamsohbat Backend", version="2.3", lifespan=lifespan)

# --- ثبت روترها (با تحمل خطا تا نبود یک ماژول کل سرویس را نخواباند) ---
try:
    try:
        from auth import router as auth_router
    except ImportError:
        from .auth import router as auth_router
    app.include_router(auth_router)
    logger.info("✅ روتر احراز هویت ثبت شد.")
except Exception as exc:  # noqa: BLE001
    logger.error("❌ ثبت روتر auth ناموفق بود: %s", exc)

try:
    from sync import router as sync_router

    app.include_router(sync_router, prefix="/api/v1")
    logger.info("✅ روتر سینک ثبت شد.")
except Exception as exc:  # noqa: BLE001
    logger.warning("⚠️ روتر sync ثبت نشد: %s", exc)

try:
    from teams import router as teams_router

    app.include_router(teams_router, prefix="/api/v1")
    logger.info("✅ روتر تیم‌ها ثبت شد.")
except Exception as exc:  # noqa: BLE001
    logger.warning("⚠️ روتر teams ثبت نشد: %s", exc)

try:
    from admin import router as admin_router

    app.include_router(admin_router, prefix="/api/v1")
    logger.info("✅ روتر ادمین ثبت شد.")
except Exception as exc:  # noqa: BLE001
    logger.warning("⚠️ روتر admin ثبت نشد: %s", exc)


# --- مدیریت اتصالات وب‌سوکت چت ---

class ConnectionManager:
    def __init__(self):
        self.active_connections: List[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        if websocket in self.active_connections:
            self.active_connections.remove(websocket)

    async def broadcast(self, message: str):
        for connection in list(self.active_connections):
            try:
                await connection.send_text(message)
            except Exception:  # noqa: BLE001
                self.disconnect(connection)


manager = ConnectionManager()


@app.get("/api/health")
def health_check():
    """بررسی سلامت سرویس؛ وضعیت دیتابیس هم گزارش می‌شود."""
    db_status = "up"
    try:
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
    except Exception as exc:  # noqa: BLE001
        db_status = f"down: {exc}"
    return {"status": "ok", "app": "Daftar Goftogu", "database": db_status}


@app.websocket("/ws/chat")
async def websocket_endpoint(websocket: WebSocket):
    await manager.connect(websocket)
    try:
        while True:
            data = await websocket.receive_text()
            await manager.broadcast(data)
    except WebSocketDisconnect:
        manager.disconnect(websocket)
