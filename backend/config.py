from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional

class Settings(BaseSettings):
    # Database Settings
    DATABASE_URL: str = "postgresql://user:password@localhost/hamsohbat"

    # SMS Settings (Faraz SMS / IPPanel)
    SMS_USERNAME: str
    SMS_PASSWORD: str
    SMS_ORIGINATOR: str
    SMS_PATTERN_CODE: str
    SMS_API_URL: str = "https://ippanel.com/patterns/pattern"

    # App Settings
    OTP_EXPIRY_SECONDS: int = 120  # 2 minutes

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

settings = Settings()
