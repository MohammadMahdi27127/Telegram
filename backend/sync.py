import time
import logging
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from sqlalchemy.orm import Session

# مدل‌ها و نشست دیتابیس از main.py
from main import SessionLocal, Entry, Person, Slip

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/sync", tags=["Sync"])

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# مدل‌های ورودی برای پایتون
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

class SyncResponse(BaseModel):
    status: str
    message: Optional[str] = None
    saved_count: int

# اندپوینت ثبت پیام‌ها در دیتابیس دارکوب
@router.post("/entries", response_model=SyncResponse)
def sync_entries(payload: SyncPayloadRequest, db: Session = Depends(get_db)):
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
        return SyncResponse(status="success", message="پیام‌ها با موفقیت در دیتابیس سرور ثبت شدند", saved_count=saved)
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing entries: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

# اندپوینت ثبت مخاطبین و گروه‌ها
@router.post("/people", response_model=SyncResponse)
def sync_people(payload: SyncPeoplePayload, db: Session = Depends(get_db)):
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
        raise HTTPException(status_code=500, detail=str(e))

# اندپوینت ثبت فیش‌ها
@router.post("/slips", response_model=SyncResponse)
def sync_slips(payload: SyncSlipPayload, db: Session = Depends(get_db)):
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
        raise HTTPException(status_code=500, detail=str(e))
    
# مدل چت هوش مصنوعی مطابق جدول دیتابیس
class SyncAiMessageItem(BaseModel):
    id: str
    user_id: Optional[str] = None
    person_id: Optional[str] = None
    role: str                      # 'user' یا 'assistant' یا 'model'
    body: str                      # متن پیام
    image_path: Optional[str] = None
    created_at: int

class SyncAiPayload(BaseModel):
    messages: List[SyncAiMessageItem]

@router.post("/ai-messages", response_model=SyncResponse)
def sync_ai_messages(payload: SyncAiPayload, db: Session = Depends(get_db)):
    try:
        from main import AiMessage
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
        return SyncResponse(
            status="success", 
            message="پیام‌های هوش مصنوعی با موفقیت ثبت شدند", 
            saved_count=saved
        )
    except Exception as e:
        db.rollback()
        logger.error(f"Error syncing AI messages: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))