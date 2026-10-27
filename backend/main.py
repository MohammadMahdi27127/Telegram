import os
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from typing import List
from sqlalchemy import Column, String, Integer, BigInteger, Boolean, Float, ForeignKey, Text, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from .config import settings
from .auth import router as auth_router

# تنظیمات دیتابیس PostgreSQL
DATABASE_URL = settings.DATABASE_URL

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
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

# ایجاد جداول در استارت‌آپ
def init_db():
    Base.metadata.create_all(bind=engine)

app = FastAPI(title="Hamsohbat Backend")

# ثبت روترها
app.include_router(auth_router)

@app.on_event("startup")
def on_startup():
    init_db()

# مدیریت اتصالات وب‌سوکت چت
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