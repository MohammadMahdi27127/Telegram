import logging
import time
import uuid
from typing import List, Optional
from pydantic import BaseModel
from fastapi import APIRouter, Depends, HTTPException, Header, status
from sqlalchemy import or_
from sqlalchemy.orm import Session

from main import SessionLocal, User, Team, TeamMember, TeamMessage
from config import settings

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

class AddTeamMemberRequest(BaseModel):
    user_id: str
    role: str = "member"

class UpdateTeamRequest(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None

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

class SendTeamMessageRequest(BaseModel):
    sender_id: str
    content: str
    recipient_id: Optional[str] = None  # None = ارسال همگانی به همه اعضا

class TeamMessageDto(BaseModel):
    id: str
    team_id: str
    sender_id: str
    content: str
    recipient_id: Optional[str] = None
    created_at: int

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
        
        # دریافت تیم‌هایی که کاربر (چه سازنده، چه عضو عادی) در جدول TeamMember عضو آن‌هاست
        teams = (
            db.query(Team)
            .join(TeamMember, Team.id == TeamMember.team_id)
            .filter(TeamMember.user_id == user_id)
            .all()
        )

        logger.info(
            f"[GET /teams/user/{user_id}] found {len(teams)} team(s) for user_id={user_id}"
        )

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


# ۳. GET /api/v1/teams/{team_id} - دریافت جزئیات تیم
@router.get("/teams/{team_id}", response_model=TeamDto)
def get_team_details(team_id: str, db: Session = Depends(get_db)):
    """
    دریافت جزئیات کامل تیم شامل اعضا
    
    مثال: GET /api/v1/teams/team-uuid-123
    """
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم یافت نشد")
    
    members_data = db.query(TeamMember).filter(
        TeamMember.team_id == team_id
    ).all()
    
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

# ۴. POST /api/v1/teams/{team_id}/members - افزودن عضو
@router.post("/teams/{team_id}/members", response_model=TeamMemberDto)
def add_team_member(
    team_id: str,
    req: AddTeamMemberRequest,
    db: Session = Depends(get_db)
):
    """
    افزودن عضو جدید به تیم
    
    درخواست: { "user_id": "user-456", "role": "member" }
    """
    # بررسی تیم موجود
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم یافت نشد")
    
    # بررسی کاربر موجود
    user = db.query(User).filter(User.id == req.user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="کاربر یافت نشد")
    
    # بررسی عدم تکرار عضویت
    existing = db.query(TeamMember).filter(
        TeamMember.team_id == team_id,
        TeamMember.user_id == req.user_id
    ).first()
    if existing:
        raise HTTPException(status_code=400, detail="کاربر قبلاً عضو است")
    
    # افزودن عضو
    new_member = TeamMember(
        id=str(uuid.uuid4()),
        team_id=team_id,
        user_id=req.user_id,
        role=req.role or "member",
        joined_at=int(time.time() * 1000)
    )
    db.add(new_member)
    db.commit()
    
    return TeamMemberDto(
        user_id=req.user_id,
        role=req.role,
        joined_at=new_member.joined_at
    )

# ۵. DELETE /api/v1/teams/{team_id}/members/{user_id} - خروج عضو از تیم
@router.delete("/teams/{team_id}/members/{user_id}", status_code=status.HTTP_200_OK)
def remove_team_member(
    team_id: str,
    user_id: str,
    current_user_id: str = Header(..., alias="X-User-Id"),
    db: Session = Depends(get_db)
):
    """
    خروج کاربر از تیم (فقط خودِ کاربر می‌تواند از تیم خارج شود).
    سازنده تیم نمی‌تواند خارج شود و باید تیم را حذف کند.
    """
    if user_id != current_user_id:
        raise HTTPException(status_code=403, detail="امکان حذف سایر کاربران وجود ندارد.")
    
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم یافت نشد.")
    
    if team.creator_id == user_id:
        raise HTTPException(status_code=400, detail="سازنده تیم نمی‌تواند تیم را ترک کند؛ باید تیم را حذف نمایید.")
    
    membership = db.query(TeamMember).filter(
        TeamMember.team_id == team_id,
        TeamMember.user_id == user_id
    ).first()
    
    if not membership:
        raise HTTPException(status_code=404, detail="عضویت در این تیم یافت نشد.")
    
    db.delete(membership)
    db.commit()
    
    return {"status": "success", "message": "با موفقیت از تیم خارج شدید."}

# ۶. PUT /api/v1/teams/{team_id} - ویرایش تیم
@router.put("/teams/{team_id}", response_model=TeamDto)
def update_team(
    team_id: str,
    req: UpdateTeamRequest,
    db: Session = Depends(get_db)
):
    """
    ویرایش نام و توضیح تیم
    """
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم یافت نشد")
    
    if req.name:
        team.name = req.name
    if req.description is not None:
        team.description = req.description
    
    db.commit()
    
    members_data = db.query(TeamMember).filter(
        TeamMember.team_id == team_id
    ).all()
    
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

# ۷. DELETE /api/v1/teams/{team_id} - حذف تیم (فقط سازنده)
@router.delete("/teams/{team_id}", status_code=status.HTTP_200_OK)
def delete_team(
    team_id: str,
    current_user_id: str = Header(..., alias="X-User-Id"),
    db: Session = Depends(get_db)
):
    """
    حذف کامل تیم و تمام اعضا و پیام‌های آن (تنها سازنده مجاز است)
    """
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم مورد نظر یافت نشد.")
    
    # تنها سازنده تیم می‌تواند کل تیم را حذف کند
    if team.creator_id != current_user_id:
        raise HTTPException(status_code=403, detail="تنها سازنده تیم امکان حذف آن را دارد.")
    
    # حذف پیام‌های تیم
    db.query(TeamMessage).filter(TeamMessage.team_id == team_id).delete(synchronize_session=False)
    
    # حذف اعضا
    db.query(TeamMember).filter(TeamMember.team_id == team_id).delete(synchronize_session=False)
    
    # حذف تیم
    db.delete(team)
    db.commit()
    
    return {"status": "success", "message": "تیم با موفقیت حذف شد."}

# ۸. GET /api/v1/teams/{team_id}/messages - دریافت پیام‌های تیم
@router.get("/teams/{team_id}/messages", response_model=List[TeamMessageDto])
def get_team_messages(
    team_id: str,
    user_id: Optional[str] = None,
    skip: int = 0,
    limit: int = 50,
    db: Session = Depends(get_db)
):
    """
    دریافت پیام‌های اشتراک‌شده درون تیم (آخرین ۵۰ پیام).

    اگر user_id ارسال شود، کاربر فقط پیام‌های زیر را می‌بیند:
      - پیام‌های همگانی (recipient_id == None)
      - پیام‌های ارسال‌شده توسط خودش (sender_id == user_id)
      - پیام‌های خصوصی که مستقیماً به او ارسال شده‌اند (recipient_id == user_id)
    """
    # بررسی تیم موجود
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم یافت نشد")

    query = db.query(TeamMessage).filter(TeamMessage.team_id == team_id)

    if user_id:
        query = query.filter(
            or_(
                TeamMessage.recipient_id.is_(None),
                TeamMessage.sender_id == user_id,
                TeamMessage.recipient_id == user_id,
            )
        )

    # آخرین پیام‌ها را با ترتیب نزولی می‌خوانیم، سپس برای نمایش به ترتیب زمانی صعودی معکوس می‌کنیم
    messages = query.order_by(TeamMessage.created_at.desc()).offset(skip).limit(limit).all()
    messages = list(reversed(messages))

    return [
        TeamMessageDto(
            id=msg.id,
            team_id=msg.team_id,
            sender_id=msg.sender_id,
            content=msg.content,
            recipient_id=msg.recipient_id,
            created_at=msg.created_at
        )
        for msg in messages
    ]

# ۹. POST /api/v1/teams/{team_id}/messages - ارسال پیام تیمی
@router.post("/teams/{team_id}/messages", response_model=TeamMessageDto)
def send_team_message(
    team_id: str,
    req: SendTeamMessageRequest,
    db: Session = Depends(get_db)
):
    """
    ارسال پیام درون تیم

    درخواست: { "sender_id": "user-123", "content": "سلام", "recipient_id": null }
    recipient_id == None یعنی ارسال همگانی؛ در غیر این صورت پیام خصوصی به آن عضو.
    """
    # بررسی تیم موجود
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="تیم یافت نشد")
    
    # بررسی عضویت فرستنده
    member = db.query(TeamMember).filter(
        TeamMember.team_id == team_id,
        TeamMember.user_id == req.sender_id
    ).first()
    
    if not member:
        raise HTTPException(status_code=403, detail="شما عضو این تیم نیستید")
    
    # ذخیره پیام
    message = TeamMessage(
        id=str(uuid.uuid4()),
        team_id=team_id,
        sender_id=req.sender_id,
        content=req.content,
        recipient_id=req.recipient_id,
        created_at=int(time.time() * 1000)
    )
    db.add(message)

    # ثبت رویداد سینک (PTS) برای اعضای تیم داخل همان تراکنش پیش از commit
    # تا در صورت بروز خطا کل عملیات منسجم بماند و نیازی به rollback بی‌اثر بعد از commit نباشد.
    from sync import record_sync_event
    if req.recipient_id:
        recipients = [req.recipient_id]
    else:
        recipients = [
            m.user_id
            for m in db.query(TeamMember).filter(TeamMember.team_id == team_id).all()
        ]
    event_payload = {
        "id": message.id,
        "team_id": message.team_id,
        "sender_id": message.sender_id,
        "content": message.content,
        "recipient_id": message.recipient_id,
        "created_at": message.created_at,
    }
    for uid in set(recipients):
        record_sync_event(db, uid, "team_message", message.id, event_payload, "upsert")

    db.commit()

    return TeamMessageDto(
        id=message.id,
        team_id=message.team_id,
        sender_id=message.sender_id,
        content=message.content,
        recipient_id=message.recipient_id,
        created_at=message.created_at
    )
