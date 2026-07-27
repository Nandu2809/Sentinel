import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    APP_NAME: str = "Sentinel AI Anomaly Detection Engine"
    API_V1_STR: str = "/api/v1/ai"
    ENV: str = os.getenv("ENV", "development")
    
    # Storage & Database
    MODEL_DIR: str = os.getenv("MODEL_DIR", os.path.join(os.path.dirname(os.path.dirname(__file__)), "saved_models"))
    DB_PATH: str = os.getenv("DB_PATH", os.path.join(os.path.dirname(os.path.dirname(__file__)), "sentinel_ai.db"))
    
    # Model Thresholds
    ANOMALY_THRESHOLD_HIGH: float = 75.0
    ANOMALY_THRESHOLD_CRITICAL: float = 85.0

    class Config:
        case_sensitive = True

settings = Settings()
