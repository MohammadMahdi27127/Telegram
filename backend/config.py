from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional

class Settings(BaseSettings):
    # Database Settings
    DATABASE_URL: str = "postgresql://user:password@localhost/hamsohbat"

    # Admin Credentials
    ADMIN_PHONE: str = "09052893115"
    ADMIN_PASSWORD: str = "138727127138727127"

    # SMS Settings (Faraz SMS / IPPanel)
    SMS_USERNAME: str = "your_username"
    SMS_PASSWORD: str = "your_password"
    SMS_API_KEY: str = "wbprpwbxQg"
    SMS_ORIGINATOR: str = "3000xxxxxx"
    SMS_PATTERN_CODE: str = "your_pattern_code"
    SMS_API_URL: str = "https://ippanel.com/patterns/pattern"

    # App Settings
    OTP_EXPIRY_SECONDS: int = 120  # 2 minutes

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

settings = Settings()
