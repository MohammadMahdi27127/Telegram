import secrets
import time
from typing import Dict, Optional
from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field

try:
    from sms_service import SMSService
    from config import settings
except ImportError:
    from .sms_service import SMSService
    from .config import settings

router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])

# In-memory storage for OTPs. 
# Key: phone number, Value: {"code": str, "expires_at": float}
# Note: For production, use Redis instead of a global dictionary.
otp_storage: Dict[str, Dict] = {}

class OTPRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$", description="شماره موبایل با ۰۹")

class OTPVerify(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    code: str = Field(..., min_length=6, max_length=6)

class LoginRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    password: str = Field(..., min_length=8)

class SignupRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    name: str = Field(..., min_length=2)
    password: str = Field(..., min_length=8)

def generate_otp_code() -> str:
    """تولید کد ۶ رقمی امن"""
    return "".join(secrets.choice("0123456789") for _ in range(6))

@router.post("/send-otp", status_code=status.HTTP_200_OK)
async def send_otp(request: OTPRequest):
    phone = request.phone
    code = generate_otp_code()
    
    # Send SMS via Faraz SMS
    success = await SMSService.send_otp(phone, code)
    
    if not success:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="خطا در ارسال پیامک. لطفاً دوباره تلاش کنید."
        )

    # Store OTP with expiration
    expires_at = time.time() + settings.OTP_EXPIRY_SECONDS
    otp_storage[phone] = {"code": code, "expires_at": expires_at}
    
    return {
        "message": "کد تایید ارسال شد.",
        "expires_in": settings.OTP_EXPIRY_SECONDS
    }

@router.post("/verify-otp")
async def verify_otp(request: OTPVerify):
    phone = request.phone
    code = request.code
    
    stored_data = otp_storage.get(phone)
    
    if not stored_data:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="کد تایید یافت نشد یا منقضی شده است."
        )
    
    if time.time() > stored_data["expires_at"]:
        del otp_storage[phone]
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید منقضی شده است."
        )
    
    if stored_data["code"] != code:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید وارد شده اشتباه است."
        )
    
    # OTP is valid - Remove it from storage
    del otp_storage[phone]
    
    # In a real scenario, you would issue a JWT token here or log the user in.
    return {
        "message": "احراز هویت با موفقیت انجام شد.",
        "status": "success"
    }

@router.post("/login")
async def login(request: LoginRequest):
    """
    ورود کاربر با شماره تلفن و رمز عبور
    POST /api/v1/auth/login
    
    Body:
    {
        "phone": "09123456789",
        "password": "رمزعبور"
    }
    """
    from main import SessionLocal, User
    import hashlib
    
    phone = request.phone
    password = request.password
    
    db = SessionLocal()
    try:
        # جستجوی کاربر در database
        user = db.query(User).filter(User.phone == phone).first()
        
        if not user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="شماره تلفن یا رمز عبور نادرست است."
            )
        
        # تایید رمز عبور
        password_hash = hashlib.sha256(f"{user.salt}{password}".encode()).hexdigest()
        if password_hash != user.password_hash:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="شماره تلفن یا رمز عبور نادرست است."
            )
        
        # تایید اینکه کاربر معلق نشده
        if user.suspended:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="این حساب معلق شده است."
            )
        
        # موفقیت
        return {
            "message": "ورود موفق",
            "user_id": user.id,
            "phone": user.phone,
            "name": user.name,
            "plan": user.plan,
            "status": "success"
        }
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"خطای سرور: {str(e)}"
        )
    finally:
        db.close()

@router.post("/signup")
async def signup(request: SignupRequest):
    """
    ثبت نام کاربر جدید
    POST /api/v1/auth/signup
    
    Body:
    {
        "phone": "09123456789",
        "name": "محمد",
        "password": "رمزعبور"
    }
    """
    from main import SessionLocal, User
    import hashlib
    import uuid
    
    phone = request.phone
    name = request.name
    password = request.password
    
    db = SessionLocal()
    try:
        # تایید اینکه شماره قبلا ثبت نشده
        existing = db.query(User).filter(User.phone == phone).first()
        if existing:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="این شماره تلفن قبلا ثبت شده است."
            )
        
        # ایجاد کاربر جدید
        salt = secrets.token_hex(16)
        password_hash = hashlib.sha256(f"{salt}{password}".encode()).hexdigest()
        
        new_user = User(
            id=str(uuid.uuid4()),
            phone=phone,
            salt=salt,
            password_hash=password_hash,
            name=name,
            plan="free",
            balance=0,
            suspended=False,
            banner_seen=False,
            created_at=int(time.time() * 1000),
            username=None
        )
        
        db.add(new_user)
        db.commit()
        db.refresh(new_user)
        
        return {
            "message": "ثبت نام موفق",
            "user_id": new_user.id,
            "phone": new_user.phone,
            "name": new_user.name,
            "status": "success"
        }
        
    except HTTPException:
        db.rollback()
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"خطای سرور: {str(e)}"
        )
    finally:
        db.close()
