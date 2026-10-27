import secrets
import time
from typing import Dict, Optional
from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field
from .sms_service import SMSService
from .config import settings

router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])

# In-memory storage for OTPs. 
# Key: phone number, Value: {"code": str, "expires_at": float}
# Note: For production, use Redis instead of a global dictionary.
otp_storage: Dict[str, Dict] = {}

class OTPRequest(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$", description="Phone number starting with 09")

class OTPVerify(BaseModel):
    phone: str = Field(..., pattern=r"^09\d{9}$")
    code: str = Field(..., min_length=6, max_length=6)

def generate_otp_code() -> str:
    """Generates a secure 6-digit random code."""
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
