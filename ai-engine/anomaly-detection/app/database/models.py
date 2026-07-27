from pydantic import BaseModel, Field
from typing import Optional, Dict, Any, List
from datetime import datetime

class RequestAnalysisPayload(BaseModel):
    request_id: Optional[str] = None
    endpoint: str = Field(..., example="/api/v1/users")
    http_method: str = Field(..., example="POST")
    payload_size: int = Field(0, example=1024)
    response_size: int = Field(0, example=512)
    response_time_ms: float = Field(0.0, example=45.2)
    status_code: int = Field(200, example=200)
    client_ip: str = Field("127.0.0.1", example="192.168.1.50")
    user_id: Optional[str] = None
    login_frequency_1h: int = Field(0, example=1)

class AnomalyResult(BaseModel):
    id: str
    request_id: Optional[str]
    endpoint: str
    anomaly_score: float
    classification: str
    reason: str
    confidence: float
    details: Dict[str, Any]
    detected_at: datetime

class ModelPerformance(BaseModel):
    model_name: str
    version: str
    accuracy: float
    precision: float
    recall: float
    false_positive_rate: float
    avg_latency_ms: float
    trained_at: datetime
