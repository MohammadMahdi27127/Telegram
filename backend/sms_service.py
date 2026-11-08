import os
import secrets
import logging
import requests
from dotenv import load_dotenv

load_dotenv()

logger = logging.getLogger(__name__)

# خواندن از محیط یا مقادیر پیش‌فرض
FARAZ_API_KEY = os.getenv("FARAZ_API_KEY", "")
FARAZ_ORIGINATOR = os.getenv("FARAZ_ORIGINATOR", "90008361")
PATTERN_CODE = os.getenv("FARAZ_PATTERN_CODE", "wbprpwbxQg")

# اندپوینت معتبر و اصلی پترن سامانه فراز / IPPanel
BASE_URL = "https://api.iranpayamak.com/ws/v1/sms/simple"


def normalize_phone_number(phone: str) -> str:
    p = phone.strip()
    if p.startswith("+98"):
        p = "0" + p[3:]
    elif p.startswith("98") and len(p) == 12:
        p = "0" + p[2:]
    return p


def send_otp_sms(phone: str, code: str) -> bool:
    if not FARAZ_API_KEY:
        logger.error("[SMS] خطا: FARAZ_API_KEY تعریف نشده است.")
        return False

    normalized_phone = normalize_phone_number(phone)

    # هدر وب‌سرویس جدید فراز اس‌ام‌اس
    headers = {
        "apikey": FARAZ_API_KEY.strip(),
        "Content-Type": "application/json",
    }

    payload = {
        "code": PATTERN_CODE.strip(),
        "sender": FARAZ_ORIGINATOR.strip(),
        "recipient": normalized_phone,
        "variable": {
            "code": str(code)
        }
    }

    try:
        response = requests.post(BASE_URL, headers=headers, json=payload, timeout=10)
        
        # اگر سرور با موفقیت ۲۰۰ داد
        if response.status_code in (200, 201):
            logger.info(f"[SMS] پیامک با موفقیت به {normalized_phone} ارسال شد. پاسخ: {response.text}")
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