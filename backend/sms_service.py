import os
import logging
import requests
from dotenv import load_dotenv

load_dotenv()

logger = logging.getLogger(__name__)

FARAZ_API_KEY = os.getenv("FARAZ_API_KEY", "")
FARAZ_ORIGINATOR = os.getenv("FARAZ_ORIGINATOR", "90008361")
PATTERN_CODE = os.getenv("FARAZ_PATTERN_CODE", "wbprpwbxQg")

BASE_URL = "https://api.iranpayamak.com/ws/v1/sms/pattern"


def normalize_phone_number(phone: str) -> str:
    p = phone.strip().replace(" ", "")

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

    headers = {
        "apikey": FARAZ_API_KEY,
        "Api-Key": FARAZ_API_KEY,
        "Authorization": f"AccessKey {FARAZ_API_KEY}",
        "Accept": "application/json",
        "Content-Type": "application/json",
    }

    payload = {
        "code": PATTERN_CODE,
        "sender": FARAZ_ORIGINATOR,
        "recipient": normalized_phone,
        "variable": {
            "code": str(code).strip()
        }
    }

    try:
        response = requests.post(BASE_URL, headers=headers, json=payload, timeout=10)
        
        # تبدیل صریح به عدد صحیح برای مقایسه قطعی
        status_code = int(response.status_code)
        
        # در وب‌سرویس فراز وضعیت ۲۰۰ یا ۲۰۱ نشانه موفقیت قطعی است
        if status_code in (200, 201):
            logger.info(f"[SMS Success] پیامک با موفقیت ارسال شد: {response.text}")
            return True
        else:
            logger.error(f"[SMS Error] کد وضعیت: {status_code} - پاسخ سرور: {response.text}")
            return False

    except requests.RequestException as e:
        logger.error(f"[SMS Exception] خطای شبکه: {e}")
        return False


class SMSService:
    @staticmethod
    async def send_otp(phone: str, code: str) -> bool:
        return send_otp_sms(phone, code)