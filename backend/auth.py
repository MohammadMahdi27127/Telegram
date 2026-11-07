import hashlib
import secrets
import time
import uuid
from typing import Dict
from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field

from sms_service import SMSService
from config import settings

router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])

# حافظه موقت کدهای پیامکی
otp_storage: Dict[str, Dict] = {}


class OTPRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$", description="شماره موبایل ۱۱ رقمی با ۰۹")


class OTPVerify(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    code: str = Field(..., min_length=6, max_length=6)


class LoginRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    password: str = Field(..., min_length=6)


class SignupRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    name: str = Field(..., min_length=2)
    password: str = Field(..., min_length=6)
    code: str = Field(..., min_length=6, max_length=6, description="کد ۶ رقمی پیامک‌شده")


def generate_otp_code() -> str:
    """تولید کد اعتبارسنجی ۶ رقمی عددی و امن"""
    return "".join(secrets.choice("0123456789") for _ in range(6))


@router.post("/send-otp", status_code=status.HTTP_200_OK)
async def send_otp(request: OTPRequest):
    phone = request.phone.strip()
    current_time = time.time()

    # جلوگیری از ارسال مکرر پیامک (حداقل فاصله ۶۰ ثانیه)
    if phone in otp_storage:
        last_sent = otp_storage[phone].get("last_sent_at", 0)
        if current_time - last_sent < 60:
            remaining = int(60 - (current_time - last_sent))
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail=f"لطفاً {remaining} ثانیه دیگر مجدداً تلاش کنید."
            )

    code = generate_otp_code()

    # ارسال پیامک از طریق وب‌سرویس جدید
    success = await SMSService.send_otp(phone, code)

    if not success:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="خطا در ارسال پیامک. لطفاً دوباره تلاش کنید."
        )

    expiry_seconds = getattr(settings, "OTP_EXPIRY_SECONDS", 120)
    otp_storage[phone] = {
        "code": code,
        "expires_at": current_time + expiry_seconds,
        "last_sent_at": current_time
    }

    return {
        "message": "کد تایید ارسال شد.",
        "expires_in": expiry_seconds,
        "status": "success"
    }


@router.post("/verify-otp")
async def verify_otp(request: OTPVerify):
    phone = request.phone.strip()
    code = request.code.strip()

    stored_data = otp_storage.get(phone)

    if not stored_data:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="کد تایید یافت نشد یا منقضی شده است."
        )

    if time.time() > stored_data["expires_at"]:
        otp_storage.pop(phone, None)
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید منقضی شده است."
        )

    if stored_data["code"] != code:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید وارد شده اشتباه است."
        )

    otp_storage.pop(phone, None)

    return {
        "message": "احراز هویت با موفقیت انجام شد.",
        "status": "success"
    }


@router.post("/login")
async def login(request: LoginRequest):
    # ایمپورت داخلی برای جلوگیری قطعی از Circular Import
    from main import SessionLocal, User

    phone = request.phone.strip()
    password = request.password

    db = SessionLocal()
    try:
        user = db.query(User).filter(User.phone == phone).first()

        if not user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="شماره تلفن یا رمز عبور نادرست است."
            )

        password_hash = hashlib.sha256(f"{user.salt}{password}".encode()).hexdigest()
        if password_hash != user.password_hash:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="شماره تلفن یا رمز عبور نادرست است."
            )

        if getattr(user, "suspended", False):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="این حساب کاربری مسدود شده است."
            )

        return {
            "message": "ورود موفق",
            "user_id": user.id,
            "phone": user.phone,
            "name": user.name,
            "plan": getattr(user, "plan", "free"),
            "status": "success"
        }
    finally:
        db.close()


@router.post("/signup")
async def signup(request: SignupRequest):
    # ایمپورت داخلی برای جلوگیری قطعی از Circular Import
    from main import SessionLocal, User

    phone = request.phone.strip()
    name = request.name.strip()
    password = request.password
    code = request.code.strip()

    # ۱. اعتبارسنجی کد ارسالی
    stored_data = otp_storage.get(phone)
    if not stored_data:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید یافت نشد. لطفاً مجدداً درخواست کد پیامکی بدهید."
        )

    if time.time() > stored_data["expires_at"]:
        otp_storage.pop(phone, None)
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید منقضی شده است. لطفاً کد جدید دریافت کنید."
        )

    if stored_data["code"] != code:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="کد تایید وارد شده اشتباه است."
        )

    db = SessionLocal()
    try:
        # ۲. بررسی تکراری نبودن شماره
        existing = db.query(User).filter(User.phone == phone).first()
        if existing:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="این شماره تلفن قبلا ثبت شده است."
            )

        otp_storage.pop(phone, None)

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