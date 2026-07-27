from fastapi import APIRouter
from app.api.endpoints import status, analyze, anomalies, models_api

api_router = APIRouter()

api_router.include_router(status.router, tags=["Status"])
api_router.include_router(analyze.router, tags=["Prediction"])
api_router.include_router(anomalies.router, tags=["Anomalies"])
api_router.include_router(models_api.router, tags=["Models"])
