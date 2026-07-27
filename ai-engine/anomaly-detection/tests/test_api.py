import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_status_endpoint():
    res = client.get("/api/v1/ai/status")
    assert res.status_code == 200
    data = res.json()
    assert data["status"] == "ONLINE"
    assert "version" in data

def test_analyze_endpoint():
    payload = {
        "endpoint": "/api/v1/auth/login",
        "http_method": "POST",
        "payload_size": 256,
        "response_size": 128,
        "response_time_ms": 45.0,
        "status_code": 200,
        "login_frequency_1h": 1
    }
    res = client.post("/api/v1/ai/analyze", json=payload)
    assert res.status_code == 200
    body = res.json()
    assert body["status"] == "SUCCESS"
    assert "anomaly_score" in body["data"]

def test_anomalies_list():
    res = client.get("/api/v1/ai/anomalies")
    assert res.status_code == 200
    body = res.json()
    assert body["status"] == "SUCCESS"
    assert "anomalies" in body

def test_models_list():
    res = client.get("/api/v1/ai/models")
    assert res.status_code == 200
    body = res.json()
    assert body["status"] == "SUCCESS"
