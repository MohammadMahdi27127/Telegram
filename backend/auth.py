"""اندپوینت‌های احراز هویت با کد یک‌بارمصرف (OTP) از طریق پیامک."""

import logging
import secrets
import time
from typing import Dict, Optional

from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field

try:  # اجرای کانتینری: uvicorn main:app
    from config import settings
    from sms_service import SMSService
except ImportError:  # اجرای پکیجی: uvicorn backend.main:app
    from .config import settings
    from .sms_service import SMSService

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])

# ذخیره‌سازی موقت کدها در حافظه.
# ساختار: {phone: {"code": str, "expires_at": float, "attempts": int}}
# توجه: در محیط عملیاتی بهتر است از Redis استفاده شود.
otp_storage: Dict[str, Dict[str, object]] = {}


class OTPRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$", description="شمارهٔ موبایل با فرمت 09xxxxxxxxx")


class OTPVerify(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    code: str = Field(..., min_length=4, max_length=8)


class OTPResponse(BaseModel):
    message: str
    expires_in: int


class VerifyResponse(BaseModel):
    message: str
    status: str
    phone: str


def generate_otp_code(length: Optional[int] = None) -> str:
    """تولید کد تصادفی امن با ماژول secrets."""
    size = length or settings.OTP_LENGTH
    return "".join(secrets.choice("0123456789") for _ in range(size))


@router.post("/send-otp", response_model=OTPResponse, status_code=status.HTTP_200_OK)
async def send_otp(request: OTPRequest) -> OTPResponse:
    """ساخت کد ۶ رقمی، ارسال با پیامک و ذخیرهٔ موقت با انقضای ۲ دقیقه."""
    phone = request.phone
    code = generate_otp_code()

    sent = await SMSService.send_otp(phone, code)
    if not sent:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="خطا در ارسال پیامک. لطفاً کمی بعد دوباره تلاش کنید.",
        )

    otp_storage[phone] = {
        "code": code,
        "expires_at": time.time() + settings.OTP_EXPIRY_SECONDS,
        "attempts": 0,
    }
    return OTPResponse(message="کد تأیید ارسال شد.", expires_in=settings.OTP_EXPIRY_SECONDS)


@router.post("/verify-otp", response_model=VerifyResponse)
async def verify_otp(request: OTPVerify) -> VerifyResponse:
    """بررسی صحت و اعتبار زمانی کد واردشده توسط کاربر."""
    phone = request.phone
    code = request.code

    stored = otp_storage.get(phone)
    if not stored:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="کدی برای این شماره یافت نشد. ابتدا درخواست کد بدهید.",
        )

    if time.time() > float(stored["expires_at"]):  # type: ignore[arg-type]
        otp_storage.pop(phone, None)
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="کد تأیید منقضی شده است.")

    stored["attempts"] = int(stored["attempts"]) + 1  # type: ignore[arg-type]
    if int(stored["attempts"]) > 5:  # type: ignore[arg-type]
        otp_storage.pop(phone, None)
        raise HTTPException(
            status_code=status.HTTP_429_TOO_MANY_REQUESTS,
            detail="تعداد تلاش‌های ناموفق زیاد بود. کد جدید درخواست کنید.",
        )

    if not secrets.compare_digest(str(stored["code"]), code):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="کد تأیید واردشده اشتباه است.")

    # کد معتبر بود؛ برای جلوگیری از استفادهٔ مجدد حذف می‌شود.
    otp_storage.pop(phone, None)
    return VerifyResponse(message="احراز هویت با موفقیت انجام شد.", status="success", phone=phone)
