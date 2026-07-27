import pytest
from app.inference.predictor import AnomalyPredictor

def test_anomaly_prediction():
    predictor = AnomalyPredictor()

    normal_payload = {
        "endpoint": "/api/v1/users",
        "http_method": "GET",
        "payload_size": 120,
        "response_size": 400,
        "response_time_ms": 30.0,
        "status_code": 200,
        "login_frequency_1h": 0
    }
    result = predictor.predict(normal_payload)
    assert result["anomaly_score"] >= 0.0
    assert result["anomaly_score"] <= 100.0
    assert "reason" in result
    assert "confidence" in result

    anomalous_payload = {
        "endpoint": "/api/v1/admin/debug/exec/eval?cmd=cat%20/etc/passwd",
        "http_method": "POST",
        "payload_size": 99999,
        "response_size": 50000,
        "response_time_ms": 2500.0,
        "status_code": 500,
        "login_frequency_1h": 25
    }
    anom_result = predictor.predict(anomalous_payload)
    assert anom_result["anomaly_score"] > result["anomaly_score"]
