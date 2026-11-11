"""تنظیمات مرکزی بک‌اند.

همهٔ مقادیر حساس (اتصال دیتابیس، اطلاعات پنل پیامک و اعتبارنامهٔ ادمین) از فایل
``backend/.env`` خوانده می‌شوند. برای محیط عملیاتی (Darkube) این مقادیر باید
به‌صورت Environment Variable در داشبورد سرویس تنظیم شوند.
"""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # --- Database ---
    DATABASE_URL: str = "postgresql://user:password@localhost:5432/hamsohbat_db"

    # --- Admin (Seed) ---
    ADMIN_PHONE: str = "09052893115"
    ADMIN_PASSWORD: str = "138727127138727127"
    ADMIN_NAME: str = "محمد مهدی"
    ADMIN_USERNAME: str = "admin"

    # --- SMS (Faraz SMS / IPPanel) ---
    # توکن/کلید API پنل پیامک (همین مقدار «توکن پیامک» است)
    SMS_API_KEY: str = ""
    # نام کاربری و رمز پنل (روش قدیمی احراز هویت وب‌سرویس پترن)
    SMS_USERNAME: str = ""
    SMS_PASSWORD: str = ""
    # شمارهٔ خط خدماتی (Originator)
    SMS_ORIGINATOR: str = ""
    # کد پترن تأییدشده در پنل
    SMS_PATTERN_CODE: str = ""
    # آدرس وب‌سرویس پترن
    SMS_API_URL: str = "https://ippanel.com/patterns/pattern"
    # نام متغیر داخل پترن (در پترن شما «code» است)
    SMS_PATTERN_VARIABLE: str = "code"

    # --- OTP ---
    OTP_EXPIRY_SECONDS: int = 120  # ۲ دقیقه
    OTP_LENGTH: int = 6

    # --- App ---
    APP_NAME: str = "Hamsohbat Backend"
    DEBUG: bool = False

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")


settings = Settings()
