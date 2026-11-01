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
        url = "https://rest.iranpayamak.com/v1/Messages/SendByBaseNumber"

        headers = {
            "Content-Type": "application/json"
        }

        # پاکسازی شماره تلفن
        clean_phone = phone.strip()

        payload = {
            "apikey": str(settings.SMS_API_KEY).strip(),
            "recipient": clean_phone,
            "pattern_code": str(settings.SMS_PATTERN_CODE).strip(),
            "input_data": [
                {"code": str(code)}  # در صورتی که متغیر پترن شما در پنل %code% است
            ]
        }

        logger.info(f"Sending OTP to {clean_phone} with pattern {settings.SMS_PATTERN_CODE}")

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.post(url, json=payload, headers=headers)
                logger.info(f"IranPayamak Response [{response.status_code}]: {response.text}")

                if response.status_code in (200, 201):
                    try:
                        data = response.json()
                        # در وب‌سرویس ایران‌پیامک status ممکن است 200 عددی یا success رشته‌ای باشد
                        if data.get("status") in ("success", 200, "200") or data.get("code") in (200, "200") or "bulk_id" in data or "data" in data:
                            return True
                    except Exception:
                        return True

                logger.error(f"IranPayamak Error Body: {response.text}")
                return False

            except Exception as e:
                logger.error(f"Network or Exception in SMSService: {e}", exc_info=True)
                return False