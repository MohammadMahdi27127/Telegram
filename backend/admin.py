import logging
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import func, and_
from sqlalchemy.orm import Session

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/admin", tags=["Admin"])

def get_db():
    from main import SessionLocal
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- Pydantic Models ---

class UserStatsResponse(BaseModel):
    id: str
    phone: str
    name: Optional[str]
    plan: str
    tokens_used: int
    created_at: int
    
    class Config:
        from_attributes = True

class AdminStatsResponse(BaseModel):
    total_tokens_used: int
    total_users: int
    users: List[UserStatsResponse]

# --- Endpoints ---

@router.get("/users-stats", response_model=AdminStatsResponse)
def get_users_stats(
    query: Optional[str] = None,
    sort_by: str = "highest_usage",
    plan: str = "all",
    db: Session = Depends(get_db)
):
    """
    دریافت آمار کاربران با امکان جستجو، فیلتر و مرتب‌سازی
    
    پارامترها:
    - query: جستجو در نام، شماره تماس یا شناسه کاربر
    - sort_by: highest_usage (پیش‌فرض), lowest_usage, newest
    - plan: all (پیش‌فرض), free, plus, smart, guest
    """
    try:
        from main import User
        
        # شروع کوئری پایه
        user_query = db.query(User)
        
        # فیلتر بر اساس پلن
        if plan != "all":
            user_query = user_query.filter(User.plan == plan)
        
        # جستجو در نام، شماره تماس و شناسه
        if query:
            search_pattern = f"%{query}%"
            user_query = user_query.filter(
                User.name.ilike(search_pattern) |
                User.phone.ilike(search_pattern) |
                User.id.ilike(search_pattern)
            )
        
        # مرتب‌سازی
        if sort_by == "highest_usage":
            user_query = user_query.order_by(User.tokens_used.desc())
        elif sort_by == "lowest_usage":
            user_query = user_query.order_by(User.tokens_used.asc())
        elif sort_by == "newest":
            user_query = user_query.order_by(User.created_at.desc())
        
        users = user_query.all()
        
        # محاسبه مجموع توکن‌های استفاده‌شده
        total_tokens = db.query(func.coalesce(func.sum(User.tokens_used), 0)).scalar() or 0
        
        user_responses = [
            UserStatsResponse(
                id=user.id,
                phone=user.phone,
                name=user.name,
                plan=user.plan,
                tokens_used=user.tokens_used or 0,
                created_at=user.created_at
            )
            for user in users
        ]
        
        return AdminStatsResponse(
            total_tokens_used=int(total_tokens),
            total_users=len(users),
            users=user_responses
        )
    
    except Exception as e:
        logger.error(f"Error fetching admin stats: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
