from fastapi import APIRouter
from app.models.model_registry import ModelRegistry
from app.training.trainer import ModelTrainer

router = APIRouter()
registry = ModelRegistry()
trainer = ModelTrainer()

@router.get("/models")
def list_models():
    models = registry.list_models()
    return {"status": "SUCCESS", "models": models}

@router.post("/models/train")
def train_models(version: str = "v1.1"):
    metrics = trainer.train_pipeline(version=version)
    return {
        "status": "SUCCESS",
        "message": f"Pipeline retraining completed for version {version}",
        "metrics": metrics
    }

@router.get("/model-performance")
def get_model_performance():
    models = registry.list_models()
    return {
        "status": "SUCCESS",
        "performance": [
            {
                "model_name": m["name"],
                "version": m["version"],
                "metrics": m["metrics"],
                "status": m["status"]
            }
            for m in models
        ]
    }
