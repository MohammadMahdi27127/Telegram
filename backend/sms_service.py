import logging
import httpx

try:
    from config import settings
except ImportError:
    from .config import settings

logger = logging.getLogger(__name__)

class SMSService:
    @staticmethod
    async def send_otp(phone: str, code: str) -> bool:
        """
        ارسال پیامک با وب‌سرویس مدرن پترن فراز اس‌ام‌اس (IPPanel REST API)
        """
        url = "https://api2.ippanel.com/api/v1/sms/pattern/normal/send"

        # پاکسازی ورودی‌ها
        clean_phone = phone.strip()
        api_key = str(settings.SMS_API_KEY).strip()
        pattern_code = str(settings.SMS_PATTERN_CODE).strip()
        sender_number = str(getattr(settings, "SMS_ORIGINATOR", "+983000505")).strip()

        headers = {
            "apikey": api_key,
            "Content-Type": "application/json"
        }

        payload = {
            "code": pattern_code,
            "sender": sender_number,
            "recipient": clean_phone,
            "variable": {
                "code": str(code)  # نام متغیر در متن پترن پنل فراز (مثلاً: کد شما: %code%)
            }
        }

        logger.info(f"Sending OTP via FarazSMS to {clean_phone} using pattern {pattern_code}")

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.post(url, json=payload, headers=headers)
                logger.info(f"FarazSMS Response [{response.status_code}]: {response.text}")

                if response.status_code == 200:
                    data = response.json()
                    # بررسی خروجی IPPanel/FarazSMS
                    if data.get("status") == "OK" or "data" in data or data.get("code") == 200:
                        logger.info(f"FarazSMS: OTP sent successfully to {clean_phone}")
                        return True

                logger.error(f"FarazSMS error status {response.status_code}: {response.text}")
                return False

            except Exception as e:
                logger.error(f"Network error in FarazSMS: {e}", exc_info=True)
                return False