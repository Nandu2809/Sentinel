# Model Training & Inference Guide

## Running the Service
```bash
cd ai-engine/anomaly-detection
python -m app.main
```

## Running Unit Tests
```bash
pytest -v tests/
```

## Training Trigger
```http
POST /api/v1/ai/models/train?version=v1.1
```

## Real-Time Inference Payload Example
```json
POST /api/v1/ai/analyze
{
  "request_id": "req-9921",
  "endpoint": "/api/v1/users/admin/delete",
  "http_method": "POST",
  "payload_size": 15000,
  "response_size": 450,
  "response_time_ms": 1200.0,
  "status_code": 200,
  "client_ip": "198.51.100.42",
  "login_frequency_1h": 12
}
```

## Response Output
```json
{
  "status": "SUCCESS",
  "data": {
    "anomaly_score": 88.5,
    "classification": "CRITICAL_ANOMALY",
    "reason": "Unusually large request payload size. High server execution latency. Abnormal authentication velocity.",
    "confidence": 0.885,
    "details": {
      "model_scores": {
        "isolation_forest": 91.2,
        "one_class_svm": 85.4,
        "clustering": 87.0
      }
    }
  }
}
```
