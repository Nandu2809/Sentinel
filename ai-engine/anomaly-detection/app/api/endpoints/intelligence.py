from fastapi import APIRouter
from pydantic import BaseModel
from typing import Optional, List, Dict, Any
import datetime

router = APIRouter()

class BehavioralAnalyzeRequest(BaseModel):
    userId: Optional[str] = None
    email: Optional[str] = None
    ipAddress: Optional[str] = None
    userLocation: Optional[str] = None
    previousLocation: Optional[str] = None
    timeDeltaMinutes: Optional[int] = None
    isNewDevice: Optional[bool] = False

@router.get("/ai/intelligence")
def get_ai_intelligence(email: Optional[str] = None):
    return {
        "status": "SUCCESS",
        "data": {
            "user": email or "nanda.test@sentinel.com",
            "behaviorStatus": "ABNORMAL",
            "aiConfidence": 94,
            "anomalyScore": 92.5,
            "reason": "Login behavior differs from baseline: Impossible travel detected (India -> USA in 5 minutes)",
            "factors": [
                { "name": "Impossible Travel (India -> USA)", "impact": "HIGH", "score": 95 },
                { "name": "New Device Fingerprint", "impact": "MEDIUM", "score": 45 },
                { "name": "Unusual Login Hour", "impact": "LOW", "score": 30 }
            ],
            "timeline": [
                { "timestamp": "10:00 AM", "location": "Mumbai, IN", "status": "SAFE" },
                { "timestamp": "10:05 AM", "location": "New York, US", "status": "CRITICAL_ANOMALY" }
            ]
        }
    }

@router.post("/ai/analyze")
def analyze_behavior(request: BehavioralAnalyzeRequest):
    impossible_travel = (
        request.userLocation and request.previousLocation and
        request.userLocation != request.previousLocation and
        request.timeDeltaMinutes is not None and request.timeDeltaMinutes < 30
    )

    if impossible_travel:
        anomaly_score = 95.0
        prediction = "CRITICAL_ANOMALY"
        reason = f"Impossible travel detected: {request.previousLocation} -> {request.userLocation} in {request.timeDeltaMinutes} mins"
    elif request.isNewDevice:
        anomaly_score = 65.0
        prediction = "HIGH_RISK"
        reason = "New unverified device fingerprint detected"
    else:
        anomaly_score = 15.0
        prediction = "NORMAL"
        reason = "User activity matches historical baseline behavior"

    return {
        "status": "SUCCESS",
        "data": {
            "userId": request.userId or "usr-2460402",
            "email": request.email or "nanda.test@sentinel.com",
            "anomalyScore": anomaly_score,
            "prediction": prediction,
            "confidence": 94.0 if anomaly_score > 50 else 98.0,
            "reason": reason,
            "timestamp": datetime.datetime.utcnow().isoformat()
        }
    }
