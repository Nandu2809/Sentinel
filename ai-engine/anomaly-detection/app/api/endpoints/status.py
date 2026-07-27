from fastapi import APIRouter
from app.models.model_registry import ModelRegistry

router = APIRouter()
registry = ModelRegistry()

@router.get("/status")
def get_status():
    models = registry.list_models()
    return {
        "status": "ONLINE",
        "engine": "Sentinel AI Anomaly Detection & Threat Intelligence Engine",
        "version": "1.0.0",
        "active_models_count": len(models),
        "models": models
    }
