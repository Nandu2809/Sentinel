import uvicorn
from fastapi import FastAPI
from app.config.settings import settings
from app.database.db import init_db
from app.api.router import api_router

init_db()

app = FastAPI(
    title=settings.APP_NAME,
    description="Sentinel Intelligent AI Anomaly Detection & Behavioral Threat Engine API",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

app.include_router(api_router, prefix=settings.API_V1_STR)

@app.get("/")
def root():
    return {
        "service": settings.APP_NAME,
        "status": "RUNNING",
        "docs": "/docs"
    }

if __name__ == "__main__":
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
