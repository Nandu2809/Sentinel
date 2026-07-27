import pytest
from app.features.extractor import FeatureExtractor

def test_feature_extraction():
    extractor = FeatureExtractor()
    payload = {
        "endpoint": "/api/v1/auth/login",
        "http_method": "POST",
        "payload_size": 2048,
        "response_size": 512,
        "response_time_ms": 120.5,
        "status_code": 200,
        "login_frequency_1h": 3
    }
    feats = extractor.extract_features(payload)
    assert feats["is_post"] == 1.0
    assert feats["is_get"] == 0.0
    assert feats["endpoint_depth"] == 4.0
    assert feats["login_freq_1h"] == 3.0
    assert feats["uri_entropy"] > 0.0

    vector = extractor.to_vector(feats)
    assert vector.shape == (1, 11)
