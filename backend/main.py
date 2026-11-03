import os
import logging
import secrets
import hashlib
import time
from typing import List, Optional
from fastapi import FastAPI, WebSocket, WebSocketDisconnect, Depends
from sqlalchemy.orm import Session
import uuid
from contextlib import asynccontextmanager
from typing import List

from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from sqlalchemy import (
    Column, String, Integer, BigInteger, Boolean, Float, Text, create_engine
)
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from pydantic import BaseModel

# تنظیم لاگر در بالاترین نقطه فایل
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# بارگذاری تنظیمات و روتر احراز هویت
try:
    from config import settings
    from auth import router as auth_router
except ImportError as e:
    import sys
    logger.error(f"Import error: {e}", exc_info=True)
    raise RuntimeError(f"Failed to import modules: {e}")

# تنظیم آدرس اتصال به پایگاه داده PostgreSQL دارکوب
def get_database_url(url: str) -> str:
    """Convert postgres:// to postgresql+psycopg2:// for SQLAlchemy 2.0+ compatibility."""
    if url.startswith("postgres://"):
        url = url.replace("postgres://", "postgresql+psycopg2://", 1)
    elif url.startswith("postgresql://"):
        url = url.replace("postgresql://", "postgresql+psycopg2://", 1)
    return url

DATABASE_URL = get_database_url(settings.DATABASE_URL)
logger.info("Database URL configured (masked): postgresql+psycopg2://***")

try:
    engine = create_engine(DATABASE_URL, echo=False, pool_pre_ping=True)
    SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
except Exception as e:
    logger.error(f"Failed to create database engine: {e}")
    DATABASE_URL = "sqlite:///./hamsohbat.db"
    engine = create_engine(DATABASE_URL, echo=False)
    SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    logger.info("Fell back to SQLite database")

Base = declarative_base()

# --- مدل‌های جدول‌های پایگاه داده PostgreSQL ---

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
    username = Column(String, unique=True, nullable=True, index=True)
    created_at = Column(BigInteger)
    monthly_download_count = Column(Integer, default=0)
    monthly_ai_usage_count = Column(Integer, default=0)
    last_quota_reset_month = Column(String)
    tokens_used = Column(BigInteger, default=0)

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

class Team(Base):
    __tablename__ = "teams"
    id = Column(String, primary_key=True)
    name = Column(String, nullable=False)
    description = Column(Text)
    creator_id = Column(String, index=True)
    created_at = Column(BigInteger, nullable=False)

class TeamMember(Base):
    __tablename__ = "team_members"
    id = Column(String, primary_key=True)
    team_id = Column(String, index=True)
    user_id = Column(String, index=True)
    role = Column(String, default="member")
    joined_at = Column(BigInteger, nullable=False)

class AdminAiChat(Base):
    __tablename__ = "admin_ai_chats"
    id = Column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    admin_id = Column(String, nullable=False, index=True)
    role = Column(String(16), nullable=False)  # 'user' یا 'assistant'
    content = Column(Text, nullable=False)
    created_at = Column(BigInteger, default=lambda: int(time.time() * 1000))

class TeamMessage(Base):
    __tablename__ = "team_messages"
    id = Column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    team_id = Column(String, nullable=False, index=True)
    sender_id = Column(String, nullable=False, index=True)
    content = Column(Text, nullable=False)
    created_at = Column(BigInteger, nullable=False, default=lambda: int(time.time() * 1000))

# متد ساخت جداول
def init_db():
    try:
        logger.info("Initializing database tables...")
        Base.metadata.create_all(bind=engine)
        
        # Database migrations
        db = SessionLocal()
        try:
            # Ensure tokens_used column exists (PostgreSQL migration)
            db.execute("""
                ALTER TABLE users ADD COLUMN IF NOT EXISTS tokens_used BIGINT DEFAULT 0;
            """)
            logger.info("tokens_used column ensured in users table")
            
            # Ensure username column exists with unique constraint (PostgreSQL migration)
            db.execute("""
                ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(64) UNIQUE;
            """)
            logger.info("username column ensured in users table")
            
            # Create admin_ai_chats table if not exists
            db.execute("""
                CREATE TABLE IF NOT EXISTS admin_ai_chats (
                    id VARCHAR(64) PRIMARY KEY,
                    admin_id VARCHAR(64) NOT NULL,
                    role VARCHAR(16) NOT NULL,
                    content TEXT NOT NULL,
                    created_at BIGINT NOT NULL
                );
            """)
            logger.info("admin_ai_chats table ensured")
            
            # Create index on admin_id for faster queries
            db.execute("""
                CREATE INDEX IF NOT EXISTS idx_admin_ai_chats_admin_id ON admin_ai_chats(admin_id);
            """)
            logger.info("admin_ai_chats index ensured")
            
            # Create team_messages table if not exists
            db.execute("""
                CREATE TABLE IF NOT EXISTS team_messages (
                    id VARCHAR(64) PRIMARY KEY,
                    team_id VARCHAR(64) NOT NULL,
                    sender_id VARCHAR(64) NOT NULL,
                    content TEXT NOT NULL,
                    created_at BIGINT NOT NULL
                );
            """)
            logger.info("team_messages table ensured")
            
            # Create indexes on team_messages
            db.execute("""
                CREATE INDEX IF NOT EXISTS idx_team_messages_team_id ON team_messages(team_id);
            """)
            db.execute("""
                CREATE INDEX IF NOT EXISTS idx_team_messages_created_at ON team_messages(created_at);
            """)
            logger.info("team_messages indexes ensured")
            
            db.commit()
        except Exception as e:
            logger.warning(f"Could not add/update columns: {e}")
            db.rollback()
        finally:
            db.close()
        
        logger.info("Database tables created successfully.")
    except Exception as e:
        logger.error(f"Database initialization failed: {e}")

def hash_password(password: str, salt: str) -> str:
    return hashlib.sha256(f"{salt}{password}".encode()).hexdigest()

def create_admin_user():
    db = SessionLocal()
    try:
        admin_phone = "09052893115"
        admin_password = "138727127"
        admin_name = "محمد مهدی"
        
        existing_admin = db.query(User).filter(User.phone == admin_phone).first()
        if existing_admin:
            logger.info(f"Admin user already exists: {existing_admin.phone}")
            return
        
        salt = secrets.token_hex(16)
        password_hash = hash_password(admin_password, salt)
        
        admin_user = User(
            id=str(uuid.uuid4()),
            phone=admin_phone,
            salt=salt,
            password_hash=password_hash,
            name=admin_name,
            plan="smart",
            balance=1000000,
            suspended=False,
            banner_seen=True,
            created_at=int(time.time() * 1000),
            username="admin"
        )
        
        db.add(admin_user)
        db.commit()
        logger.info("Admin user created successfully.")
    except Exception as e:
        logger.error(f"Error creating admin user: {e}", exc_info=True)
        db.rollback()
    finally:
        db.close()

@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    create_admin_user()
    yield
    if engine:
        engine.dispose()

app = FastAPI(title="Hamsohbat Backend", lifespan=lifespan)

# روتر احراز هویت
app.include_router(auth_router)

# روتر همگام‌سازی (ایمپورت در این بخش مانع بروز خطای چرخه‌ای می‌شود)
from sync import router as sync_router
app.include_router(sync_router, prefix="/api/v1")

# روتر تیم‌ها
from teams import router as teams_router
app.include_router(teams_router, prefix="/api/v1")

# روتر ادمین
from admin import router as admin_router
app.include_router(admin_router, prefix="/api/v1")

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
    return {"status": "ok", "app": "Daftar Goftogu"}

@app.websocket("/ws/chat")
async def websocket_endpoint(websocket: WebSocket):
    await manager.connect(websocket)
    try:
        while True:
            data = await websocket.receive_text()
            await manager.broadcast(data)
    except WebSocketDisconnect:
        manager.disconnect(websocket)

class UserCheckRequest(BaseModel):
    phone: str

class UserCheckResponse(BaseModel):
    exists: bool
    user_id: Optional[str] = None
    name: Optional[str] = None
    username: Optional[str] = None

@app.post("/api/v1/users/check", response_model=UserCheckResponse)
def check_user_exists(payload: UserCheckRequest, db: Session = Depends(lambda: SessionLocal())):
    clean_phone = payload.phone.strip().replace(" ", "")
    user = db.query(User).filter(User.phone == clean_phone).first()
    if user:
        return UserCheckResponse(
            exists=True,
            user_id=user.id,
            name=user.name,
            username=user.username
        )
    return UserCheckResponse(exists=False)


class UpdateUsernameRequest(BaseModel):
    username: str

class UserProfileResponse(BaseModel):
    user_id: str
    phone: str
    name: str
    username: Optional[str] = None
    plan: str
    balance: int
    plan_expires_at: Optional[int] = None
    suspended: bool

@app.get("/api/v1/users/{user_id}/profile", response_model=UserProfileResponse)
def get_user_profile(user_id: str, db: Session = Depends(lambda: SessionLocal())):
    """
    دریافت پروفایل کاربر شامل plan، balance، و وضعیت اشتراک
    
    مثال: GET /api/v1/users/123e4567-e89b-12d3-a456-426614174000/profile
    """
    from fastapi import HTTPException
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر یافت نشد")
    return UserProfileResponse(
        user_id=user.id,
        phone=user.phone or "",
        name=user.name or "",
        username=user.username,
        plan=user.plan or "free",
        balance=user.balance or 0,
        plan_expires_at=user.plan_expires_at,
        suspended=user.suspended or False
    )


@app.put("/api/v1/users/{user_id}/username")
def update_username(user_id: str, req: UpdateUsernameRequest, db: Session = Depends(lambda: SessionLocal())):
    """
    آپڈیٹ نام کاربری (Username/Handle)
    
    درخواست: PUT /api/v1/users/{user_id}/username
    بدنه: { "username": "mohammad_ali" }
    
    اعتبارسنجی:
    - حداقل ۳ و حداکثر ۳۰ کاراکتر
    - فقط حروف انگلیسی a-z، اعداد 0-9 و خط زیرین _
    - حذف خودکار @ از ابتدا و تبدیل به کوچک
    """
    from fastapi import HTTPException
    import re
    
    # اعتبارسنجی ورودی
    username = req.username.strip().lstrip('@').lower() if req.username else ""
    
    if not username:
        raise HTTPException(status_code=400, detail="نام کاربری خالی است")
    
    if len(username) < 3 or len(username) > 30:
        raise HTTPException(status_code=400, detail="نام کاربری باید ۳ تا ۳۰ کاراکتر باشد")
    
    # بررسی فرمت معتبر
    if not re.match(r"^[a-z0-9_]+$", username):
        raise HTTPException(status_code=400, detail="نام کاربری فقط می‌تواند حروف انگلیسی کوچک، اعداد و خط زیرین را شامل باشد")
    
    # بررسی موجود بودن کاربر
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر یافت نشد")
    
    # بررسی تکراری نبودن username
    existing_user = db.query(User).filter(
        User.username == username,
        User.id != user_id  # خود این کاربر را exclude کن
    ).first()
    if existing_user:
        raise HTTPException(status_code=400, detail="این نام کاربری قبلاً انتخاب شده است")
    
    # ذخیره نام کاربری
    user.username = username
    db.commit()
    
    return {
        "user_id": user.id,
        "username": user.username,
        "message": "نام کاربری با موفقیت به‌روزرسانی شد"
    }


# --- اندپوینت‌های چت هوشمند ادمین ---

class AdminAiChatMessage(BaseModel):
    id: str
    role: str
    content: str
    created_at: int

class AdminAiChatRequest(BaseModel):
    admin_id: str
    message: str

class AdminAiChatResponse(BaseModel):
    reply: str

@app.get("/api/v1/admin/ai/history")
def get_admin_ai_history(admin_id: str, db: Session = Depends(lambda: SessionLocal())):
    """
    دریافت تاریخچه پیام‌های چت هوشمند ادمین
    
    مثال: GET /api/v1/admin/ai/history?admin_id=123e4567
    """
    from fastapi import HTTPException
    
    # بررسی اینکه آیا کاربر ادمین است
    user = db.query(User).filter(User.id == admin_id).first()
    if not user or user.phone != "09052893115":
        raise HTTPException(status_code=403, detail="دسترسی رد شد")
    
    # دریافت تاریخچه
    messages = db.query(AdminAiChat).filter(
        AdminAiChat.admin_id == admin_id
    ).order_by(AdminAiChat.created_at.asc()).all()
    
    return [
        AdminAiChatMessage(
            id=msg.id,
            role=msg.role,
            content=msg.content,
            created_at=msg.created_at
        )
        for msg in messages
    ]

@app.post("/api/v1/admin/ai/chat")
def send_admin_ai_message(req: AdminAiChatRequest, db: Session = Depends(lambda: SessionLocal())):
    """
    ارسال پیام به دستیار هوشمند مدیریت و دریافت پاسخ
    
    درخواست: POST /api/v1/admin/ai/chat
    بدنه: { "admin_id": "123e4567", "message": "لطفاً گزارش کاربران فعال بده" }
    """
    from fastapi import HTTPException
    
    # بررسی اینکه آیا کاربر ادمین است
    user = db.query(User).filter(User.id == req.admin_id).first()
    if not user or user.phone != "09052893115":
        raise HTTPException(status_code=403, detail="دسترسی رد شد")
    
    # ذخیره پیام کاربر
    user_message = AdminAiChat(
        id=str(uuid.uuid4()),
        admin_id=req.admin_id,
        role="user",
        content=req.message,
        created_at=int(time.time() * 1000)
    )
    db.add(user_message)
    db.flush()  # برای اطمینان از ذخیره‌سازی قبل از تولید پاسخ
    
    # تولید پاسخ از مدل هوش مصنوعی
    try:
        ai_response = generate_admin_ai_response(req.message)
    except Exception as e:
        logger.error(f"AI generation failed: {e}")
        ai_response = "متأسفانه در این لحظه نتوانستم پاسخ دهم. لطفاً دوباره تلاش کنید."
    
    # ذخیره پاسخ
    assistant_message = AdminAiChat(
        id=str(uuid.uuid4()),
        admin_id=req.admin_id,
        role="assistant",
        content=ai_response,
        created_at=int(time.time() * 1000)
    )
    db.add(assistant_message)
    db.commit()
    
    return AdminAiChatResponse(reply=ai_response)

def generate_admin_ai_response(message: str) -> str:
    """
    تولید پاسخ دستیار هوشمند مدیریت
    می‌تواند از OpenAI، Claude یا مدل محلی استفاده کند
    """
    import os
    
    # نمونه ساده - می‌تواند با API واقعی جایگزین شود
    system_prompt = """You are an Executive System Copilot for the Admin of DaftarGoftogu. 
You have direct context of system stats, users, and platform mechanics. 
Speak formally, analytically, and provide direct technical and management insights. 
Always respond in Persian (Farsi)."""
    
    # اگر OpenAI API موجود است، استفاده کنید
    openai_key = os.getenv("OPENAI_API_KEY")
    if openai_key:
        try:
            import openai
            openai.api_key = openai_key
            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": message}
                ],
                temperature=0.7,
                max_tokens=500
            )
            return response.choices[0].message.content
        except Exception as e:
            logger.warning(f"OpenAI API error: {e}")
    
    # فال‌بک: پاسخ ساده
    return f"تشکر از پیام شما. من به عنوان دستیار مدیریت در خدمتم برای کمک به:\n- تجزیه‌تحلیل آمار کاربران\n- نظارت بر سیستم\n- ارائه توصیه‌های بهبود\n\nپیام شما: {message[:100]}"
