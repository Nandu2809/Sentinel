from fastapi import APIRouter
from app.database.models import RequestAnalysisPayload
from app.inference.predictor import AnomalyPredictor

router = APIRouter()
predictor = AnomalyPredictor()

@router.post("/analyze")
def analyze_request(payload: RequestAnalysisPayload):
    result = predictor.predict(payload.dict())
    return {
        "status": "SUCCESS",
        "data": result
    }
