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
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Api-Key": FARAZ_API_KEY.strip(),
    }

    payload = {
        "code": PATTERN_CODE.strip(),
        "attributes": {
            "code": str(code),
        },
        "recipient": normalized_phone,
        "line_number": FARAZ_ORIGINATOR.strip(),
        "number_format": "english",
    }

    try:
        response = requests.post(
            BASE_URL,
            headers=headers,
            json=payload,
            timeout=10,
        )

        if response.status_code == 201:
            logger.info(
                "[SMS] پیامک OTP با موفقیت به %s ارسال شد. پاسخ: %s",
                normalized_phone,
                response.text,
            )
            return True

        logger.error(
            "[SMS Error] کد وضعیت: %s - پاسخ سرور: %s",
            response.status_code,
            response.text,
        )
        return False

    except requests.RequestException as e:
        logger.error("[SMS Exception] خطای شبکه در ارسال پیامک: %s", e)
        return False


class SMSService:
    @staticmethod
    async def send_otp(phone: str, code: str) -> bool:
        return send_otp_sms(phone, code)