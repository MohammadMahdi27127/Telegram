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
        ارسال کد اعتبارسنجی با استفاده از وب‌سرویس مدرن پترن فراز اس‌ام‌اس (IPPANEL REST API)
        """
        # آدرس استاندارد و نوین پترن در فراز اس‌ام‌اس
        url = "https://api2.ippanel.com/api/v1/sms/pattern/normal/send"
        
        headers = {
            "apikey": settings.SMS_API_KEY,
            "Content-Type": "application/json"
        }
        
        payload = {
            "code": settings.SMS_PATTERN_CODE,
            "sender": settings.SMS_ORIGINATOR,
            "recipient": phone,
            "variable": {
                "code": str(code)  # متغیری که در متن پترن پنل مشخص کردی (%code%)
            }
        }

        async with httpx.AsyncClient(timeout=10.0) as client:
            try:
                response = await client.post(url, json=payload, headers=headers)
                
                # بررسی موفقیت‌آمیز بودن پاسخ (کد وضعیت ۲۰۰)
                if response.status_code == 200:
                    data = response.json()
                    logger.info(f"SMS successfully sent to {phone}. Response: {data}")
                    return True
                else:
                    logger.error(f"Failed to send SMS to {phone}. Status: {response.status_code}, Body: {response.text}")
                    return False
                    
            except httpx.RequestError as exc:
                logger.error(f"Network error while requesting Faraz SMS: {exc}")
                return False
            except Exception as e:
                logger.error(f"Unexpected error in SMSService: {str(e)}")
                return False