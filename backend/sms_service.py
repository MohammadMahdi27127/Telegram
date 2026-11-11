"""سرویس ارسال پیامک (فراز اس‌ام‌اس / IPPanel).

روش کار وب‌سرویس پترن:
    POST https://ippanel.com/patterns/pattern
    Content-Type: application/x-www-form-urlencoded
    user=...&pass=...&from=...&to=...&pattern_code=...&input_data={"code":"1234"}

اگر ``SMS_API_KEY`` تنظیم شده باشد، توکن در هدر ``Authorization`` ارسال می‌شود
(روش جدید پنل)؛ در غیر این صورت از نام کاربری/رمز پنل استفاده می‌شود.
"""

import json
import logging
from typing import Optional

import httpx

from config import settings

logger = logging.getLogger(__name__)


class SMSService:
    """کلاینت آسنکرون ارسال پیامک الگومحور (Pattern)."""

    @staticmethod
    def _build_headers() -> dict[str, str]:
        headers = {"Content-Type": "application/x-www-form-urlencoded"}
        if settings.SMS_API_KEY:
            # توکن پیامک در این هدر ارسال می‌شود.
            headers["Authorization"] = f"AccessKey {settings.SMS_API_KEY}"
        return headers

    @staticmethod
    async def send_otp(phone: str, code: str, variable: Optional[str] = None) -> bool:
        """ارسال کد یک‌بارمصرف با پترن تأییدشده.

        Args:
            phone: شمارهٔ گیرنده (مثلاً 09121234567)
            code: کد ۶ رقمی تولیدشده
            variable: نام متغیر پترن؛ پیش‌فرض از تنظیمات خوانده می‌شود.

        Returns:
            True در صورت ارسال موفق، در غیر این صورت False.
        """
        variable_name = variable or settings.SMS_PATTERN_VARIABLE

        if not settings.SMS_PATTERN_CODE or not settings.SMS_ORIGINATOR:
            logger.error("پیکربندی پیامک ناقص است: SMS_PATTERN_CODE یا SMS_ORIGINATOR تنظیم نشده.")
            return False

        payload: dict[str, str] = {
            "from": settings.SMS_ORIGINATOR,
            "to": phone,
            "pattern_code": settings.SMS_PATTERN_CODE,
            # مقادیر متغیر پترن باید به‌صورت رشتهٔ JSON ارسال شوند.
            "input_data": json.dumps({variable_name: code}, ensure_ascii=False),
        }
        # روش قدیمی احراز هویت (در صورت نبود توکن)
        if settings.SMS_USERNAME:
            payload["user"] = settings.SMS_USERNAME
        if settings.SMS_PASSWORD:
            payload["pass"] = settings.SMS_PASSWORD

        try:
            async with httpx.AsyncClient(timeout=15.0) as client:
                response = await client.post(
                    settings.SMS_API_URL,
                    data=payload,
                    headers=SMSService._build_headers(),
                )
                response.raise_for_status()
                body = (response.text or "").strip()
                logger.info("پیامک برای %s ارسال شد. پاسخ سرور: %s", phone, body)
                # وب‌سرویس در صورت موفقیت، شناسهٔ عددی پیام را برمی‌گرداند.
                return body.lstrip("-").isdigit() and not body.startswith("-")
        except httpx.HTTPStatusError as exc:
            logger.error("خطای HTTP در ارسال پیامک: %s | %s", exc.response.status_code, exc.response.text)
            return False
        except Exception as exc:  # noqa: BLE001
            logger.error("خطای غیرمنتظره در ارسال پیامک: %s", exc)
            return False
