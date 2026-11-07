import os
import secrets
import logging
import requests
from dotenv import load_dotenv

load_dotenv()

logger = logging.getLogger(__name__)

# خواندن متغیرها از محیط (بدون تغییر شماره ارسال)
FARAZ_API_KEY = os.getenv("FARAZ_API_KEY", "")
FARAZ_ORIGINATOR = os.getenv("FARAZ_ORIGINATOR", "90008361")
PATTERN_CODE = os.getenv("FARAZ_PATTERN_CODE", "wbprpwbxQg")

# آدرس وب‌سرویس جدید اعلام‌شده توسط پشتیبانی
BASE_URL = "https://api.iranpayamak.com/api/v1/sms/pattern/normal/send"


def generate_otp_code(length: int = 6) -> str:
    """تولید کد اعتبارسنجی عددی و ایمن"""
    return "".join(secrets.choice("0123456789") for _ in range(length))


def normalize_phone_number(phone: str) -> str:
    """استانداردسازی فرمت شماره تلفن به فرمت 09..."""
    p = phone.strip()
    if p.startswith("+98"):
        p = "0" + p[3:]
    elif p.startswith("98") and len(p) == 12:
        p = "0" + p[2:]
    return p


def send_otp_sms(phone: str, code: str) -> bool:
    """
    ارسال پیامک پترن بر اساس وب‌سرویس جدید api.iranpayamak.com
    """
    if not FARAZ_API_KEY:
        logger.error("[SMS] خطا: FARAZ_API_KEY تعریف نشده است.")
        return False

    normalized_phone = normalize_phone_number(phone)

    headers = {
        "Api-Key": FARAZ_API_KEY,
        "Accept": "application/json",
        "Content-Type": "application/json",
    }

    payload = {
        "code": PATTERN_CODE,
        "sender": FARAZ_ORIGINATOR,
        "recipient": normalized_phone,
        "attributes": {
            "code": str(code)
        },
    }

    try:
        response = requests.post(BASE_URL, headers=headers, json=payload, timeout=10)
        
        if response.status_code in (200, 201):
            logger.info(f"[SMS] پیامک با موفقیت به {normalized_phone} ارسال شد.")
            return True
        else:
            logger.error(f"[SMS Error] کد وضعیت: {response.status_code} - پاسخ سرور: {response.text}")
            return False

    except requests.RequestException as e:
        logger.error(f"[SMS Exception] خطای شبکه در ارسال پیامک: {e}")
        return False


class SMSService:
    @staticmethod
    async def send_otp(phone: str, code: str) -> bool:
        return send_otp_sms(phone, code)