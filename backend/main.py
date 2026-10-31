import os
import logging
import secrets
import hashlib
import time
import uuid
import sync
from contextlib import asynccontextmanager
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from typing import List
from sqlalchemy import Column, String, Integer, BigInteger, Boolean, Float, ForeignKey, Text, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# Handle both Docker (absolute import) and local (relative import)
try:
    from config import settings
    from auth import router as auth_router
    from sync import router as sync_router  # <--- اضافه شد
except ImportError as e:
    import sys
    logger.error(f"Import error: {e}", exc_info=True)
    raise RuntimeError(f"Failed to import modules: {e}")

logger = logging.getLogger(__name__)

# تنظیمات دیتابیس PostgreSQL - با تبدیل ایمن postgres:// به postgresql+psycopg2://
def get_database_url(url: str) -> str:
    """Convert postgres:// to postgresql+psycopg2:// for SQLAlchemy 2.0+ compatibility."""
    if url.startswith("postgres://"):
        url = url.replace("postgres://", "postgresql+psycopg2://", 1)
    elif url.startswith("postgresql://"):
        url = url.replace("postgresql://", "postgresql+psycopg2://", 1)
    return url

DATABASE_URL = get_database_url(settings.DATABASE_URL)
logger.info(f"Database URL configured (masked): postgresql+psycopg2://***")

try:
    engine = create_engine(DATABASE_URL, echo=False, pool_pre_ping=True)
    SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
except Exception as e:
    logger.error(f"Failed to create database engine: {e}")
    # Fallback to SQLite if PostgreSQL fails
    DATABASE_URL = "sqlite:///./hamsohbat.db"
    engine = create_engine(DATABASE_URL, echo=False)
    SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    logger.info("Fell back to SQLite database")

Base = declarative_base()

# --- مدل‌های دیتابیس (مطابق با اندروید) ---

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

class Person(Base):
    __tablename__ = "people"
    id = Column(String, primary_key=True)
    user_id = Column(String, index=True)
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
    user_id = Column(String, index=True)
    person_id = Column(String, index=True)
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

# ایجاد جداول در استارت‌آپ - با try/except برای جلوگیری از CrashLoopBackOff
def init_db():
    """Initialize database tables with retry logic."""
    try:
        logger.info("Initializing database tables...")
        Base.metadata.create_all(bind=engine)
        logger.info("Database tables created successfully.")
    except Exception as e:
        logger.error(f"Database initialization failed: {e}")
        logger.warning("Database may not be ready yet. Will retry on next request.")

def hash_password(password: str, salt: str) -> str:
    """Hash password with salt using SHA-256."""
    return hashlib.sha256(f"{salt}{password}".encode()).hexdigest()

def create_admin_user():
    """Create admin user in database if not exists."""
    db = SessionLocal()
    try:
        admin_phone = "09052893115"
        admin_password = "138727127"
        admin_name = "محمد مهدی"
        
        # Check if admin already exists
        existing_admin = db.query(User).filter(User.phone == admin_phone).first()
        
        if existing_admin:
            logger.info(f"✅ Admin user already exists: {existing_admin.phone} ({existing_admin.name})")
            db.close()
            return
        
        # Create new admin user
        salt = secrets.token_hex(16)
        password_hash = hash_password(admin_password, salt)
        
        admin_user = User(
            id=str(uuid.uuid4()),
            phone=admin_phone,
            salt=salt,
            password_hash=password_hash,
            name=admin_name,
            plan="admin",
            balance=1000000,
            suspended=False,
            banner_seen=True,
            created_at=int(time.time() * 1000),
            username="admin"
        )
        
        db.add(admin_user)
        db.commit()
        db.refresh(admin_user)
        
        logger.info(f"✅ Admin user created successfully!")
        logger.info(f"   📱 Phone: {admin_user.phone}")
        logger.info(f"   👤 Name: {admin_user.name}")
        logger.info(f"   🆔 ID: {admin_user.id}")
        logger.info(f"   💎 Plan: {admin_user.plan}")
        logger.info(f"   💰 Balance: {admin_user.balance:,} tomans")
        
    except Exception as e:
        logger.error(f"❌ Error creating admin user: {e}", exc_info=True)
        db.rollback()
    finally:
        db.close()

@asynccontextmanager
async def lifespan(app: FastAPI):
    """FastAPI lifespan context manager for startup/shutdown events."""
    # Startup
    logger.info("╔═══════════════════════════════════════════════════════╗")
    logger.info("║     FastAPI Application Starting...                  ║")
    logger.info("╚═══════════════════════════════════════════════════════╝")
    
    try:
        logger.info("🔧 Initializing database...")
        init_db()
        logger.info("✅ Database initialization completed.")
        
        logger.info("👨‍💼 Setting up admin user...")
        create_admin_user()
        logger.info("✅ Admin user setup completed.")
        
    except Exception as e:
        logger.error(f"❌ Startup error (non-fatal): {e}", exc_info=True)
        logger.warning("⚠️  Application will continue, but database may not be ready.")
    
    yield
    
    # Shutdown
    logger.info("╔═══════════════════════════════════════════════════════╗")
    logger.info("║     FastAPI Application Shutting Down...             ║")
    logger.info("╚═══════════════════════════════════════════════════════╝")
    try:
        if engine:
            engine.dispose()
            logger.info("✅ Database engine disposed.")
    except Exception as e:
        logger.error(f"❌ Error during shutdown: {e}")

app = FastAPI(title="Hamsohbat Backend", lifespan=lifespan)

# ثبت روترها
app.include_router(auth_router)
app.include_router(sync_router, prefix="/api/v1")
# مدیریت اتصالات وب‌سوکت چت

class ConnectionManager:
    def __init__(self):
        self.active_connections: List[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        self.active_connections.remove(websocket)

    async def broadcast(self, message: str):
        for connection in self.active_connections:
            await connection.send_text(message)

manager = ConnectionManager()

@app.get("/api/health")
def health_check():
    return {"status": "ok ", "app": "Daftar Goftogu"}

@app.websocket("/ws/chat")
async def websocket_endpoint(websocket: WebSocket):
    await manager.connect(websocket)
    try:
        while True:
            data = await websocket.receive_text()
            await manager.broadcast(data)
    except WebSocketDisconnect:
        manager.disconnect(websocket)