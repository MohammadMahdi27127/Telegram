import httpx
import logging

try:
    from config import settings
except ImportError:
    from .config import settings

logger = logging.getLogger(__name__)

class SMSService:
    @staticmethod
    async def send_otp(phone: str, code: str) -> bool:
        """
        Sends an OTP code using the IPPanel/Faraz SMS API.
        Uses SMS_API_KEY token for authentication.
        """
        payload = {
            "user": settings.SMS_USERNAME,
            "pass": settings.SMS_PASSWORD,
            "from": settings.SMS_ORIGINATOR,
            "to": phone,
            "pattern_code": settings.SMS_PATTERN_CODE,
            "input_data": {"code": code},
            "api_key": settings.SMS_API_KEY
        }

        async with httpx.AsyncClient() as client:
            try:
                response = await client.post(settings.SMS_API_URL, json=payload)
                response.raise_for_status()
                result = response.text
                # IPPanel usually returns a message ID on success (numeric string)
                logger.info(f"SMS sent to {phone}. Result: {result}")
                return True
            except httpx.HTTPStatusError as e:
                logger.error(f"HTTP error occurred while sending SMS: {e.response.text}")
                return False
            except Exception as e:
                logger.error(f"An error occurred while sending SMS: {str(e)}")
                return False
