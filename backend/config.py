from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional
import logging

logger = logging.getLogger(__name__)

class Settings(BaseSettings):
    # Database Settings
    DATABASE_URL: str = "postgresql://user:password@localhost/hamsohbat"

    # Admin Credentials
    ADMIN_PHONE: str = "09052893115"
    ADMIN_PASSWORD: str = "138727127"

    # SMS Settings (Faraz SMS / IPPanel REST API)
    SMS_API_KEY: str = ""
    SMS_ORIGINATOR: str = "9000836"
    SMS_PATTERN_CODE: str = ""

    # App Settings
    OTP_EXPIRY_SECONDS: int = 120  # 2 minutes

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

try:
    settings = Settings()
    logger.info("Settings loaded successfully from environment")
except Exception as e:
    logger.error(f"Error loading settings: {e}")
    settings = Settings()