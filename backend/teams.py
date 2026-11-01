import logging
import time
import uuid
from typing import List, Optional
from pydantic import BaseModel
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from main import SessionLocal, User, Team, TeamMember

logger = logging.getLogger(__name__)
router = APIRouter()

def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- Pydantic Models ---

class UserCheckRequest(BaseModel):
    phone: str

class UserCheckResponse(BaseModel):
    exists: bool
    user_id: Optional[str] = None
    name: Optional[str] = None

class CreateTeamRequest(BaseModel):
    name: str
    description: Optional[str] = None
    creator_id: str
    member_user_ids: List[str]

class TeamMemberDto(BaseModel):
    user_id: str
    role: str
    joined_at: int

class TeamDto(BaseModel):
    id: str
    name: str
    description: Optional[str]
    creator_id: str
    created_at: int
    member_count: int
    members: List[TeamMemberDto]

# --- Endpoints ---

@router.post("/users/check", response_model=UserCheckResponse)
def check_user_exists(req: UserCheckRequest, db: Session = Depends(get_db)):
    """
    بررسی وجود کاربر بر اساس شماره تلفن
    
    ورودی: {"phone": "09120000000"}
    خروجی (اگر موجود): {"exists": true, "user_id": "...", "name": "..."}
    خروجی (اگر موجود نیست): {"exists": false}
    """
    try:
        # تمیز کردن شماره تلفن (اختیاری)
        phone = req.phone.strip()
        
        # جستجو در دیتابیس
        user = db.query(User).filter(User.phone == phone).first()
        
        if user:
            return UserCheckResponse(
                exists=True,
                user_id=user.id,
                name=user.name
            )
        else:
            return UserCheckResponse(exists=False)
    
    except Exception as e:
        logger.error(f"Error checking user existence: {e}")
        raise HTTPException(status_code=500, detail="خطا در بررسی کاربر")

@router.post("/teams", response_model=TeamDto)
def create_team(req: CreateTeamRequest, db: Session = Depends(get_db)):
    """
    ساخت تیم جدید با اعضا
    
    ورودی:
    {
        "name": "نام تیم",
        "description": "توضیحات",
        "creator_id": "شناسه سازنده",
        "member_user_ids": ["شناسه عضو ۱", "شناسه عضو ۲"]
    }
    
    خروجی: اطلاعات تیم ایجادشده
    """
    try:
        # بررسی وجود سازنده
        creator = db.query(User).filter(User.id == req.creator_id).first()
        if not creator:
            raise HTTPException(status_code=404, detail="سازنده تیم یافت نشد")
        
        # بررسی وجود تمام اعضا
        for user_id in req.member_user_ids:
            user = db.query(User).filter(User.id == user_id).first()
            if not user:
                raise HTTPException(status_code=404, detail=f"کاربر {user_id} یافت نشد")
        
        # ایجاد تیم
        team_id = str(uuid.uuid4())
        now = int(time.time() * 1000)
        
        team = Team(
            id=team_id,
            name=req.name.strip(),
            description=req.description.strip() if req.description else None,
            creator_id=req.creator_id,
            created_at=now
        )
        db.add(team)
        db.flush()  # برای اطمینان از پذیرش تیم پیش از افزودن اعضا
        
        # افزودن سازنده به عنوان عضو
        creator_member = TeamMember(
            id=str(uuid.uuid4()),
            team_id=team_id,
            user_id=req.creator_id,
            role="creator",
            joined_at=now
        )
        db.add(creator_member)
        
        # افزودن سایر اعضا
        for user_id in req.member_user_ids:
            if user_id != req.creator_id:  # جلوگیری از تکرار سازنده
                member = TeamMember(
                    id=str(uuid.uuid4()),
                    team_id=team_id,
                    user_id=user_id,
                    role="member",
                    joined_at=now
                )
                db.add(member)
        
        db.commit()
        
        # دریافت اعضا برای پاسخ
        members_data = db.query(TeamMember).filter(TeamMember.team_id == team_id).all()
        members = [
            TeamMemberDto(
                user_id=m.user_id,
                role=m.role,
                joined_at=m.joined_at
            )
            for m in members_data
        ]
        
        return TeamDto(
            id=team.id,
            name=team.name,
            description=team.description,
            creator_id=team.creator_id,
            created_at=team.created_at,
            member_count=len(members),
            members=members
        )
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error creating team: {e}")
        db.rollback()
        raise HTTPException(status_code=500, detail="خطا در ساخت تیم")

@router.get("/teams/user/{user_id}", response_model=List[TeamDto])
def get_user_teams(user_id: str, db: Session = Depends(get_db)):
    """
    دریافت تمام تیم‌هایی که کاربر عضو آن‌ها است
    
    خروجی: لیست تیم‌ها با تعداد اعضا
    """
    try:
        # بررسی وجود کاربر
        user = db.query(User).filter(User.id == user_id).first()
        if not user:
            raise HTTPException(status_code=404, detail="کاربر یافت نشد")
        
        # دریافت تیم‌های کاربر
        team_memberships = db.query(TeamMember).filter(
            TeamMember.user_id == user_id
        ).all()
        
        team_ids = [tm.team_id for tm in team_memberships]
        teams = db.query(Team).filter(Team.id.in_(team_ids)).all() if team_ids else []
        
        result = []
        for team in teams:
            # دریافت تعداد اعضا
            members_data = db.query(TeamMember).filter(
                TeamMember.team_id == team.id
            ).all()
            
            members = [
                TeamMemberDto(
                    user_id=m.user_id,
                    role=m.role,
                    joined_at=m.joined_at
                )
                for m in members_data
            ]
            
            result.append(TeamDto(
                id=team.id,
                name=team.name,
                description=team.description,
                creator_id=team.creator_id,
                created_at=team.created_at,
                member_count=len(members),
                members=members
            ))
        
        return result
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching user teams: {e}")
        raise HTTPException(status_code=500, detail="خطا در دریافت تیم‌ها")
