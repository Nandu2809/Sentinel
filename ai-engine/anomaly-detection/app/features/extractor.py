import math
import numpy as np
from typing import Dict, Any

class FeatureExtractor:
    """
    Extracts security and behavioral numerical features from raw API traffic events.
    """

    @staticmethod
    def calculate_entropy(text: str) -> float:
        if not text:
            return 0.0
        prob = [float(text.count(c)) / len(text) for c in set(text)]
        return -sum([p * math.log2(p) for p in prob if p > 0])

    def extract_features(self, payload: Dict[str, Any]) -> Dict[str, float]:
        endpoint = str(payload.get("endpoint", "/"))
        http_method = str(payload.get("http_method", "GET")).upper()
        payload_size = float(payload.get("payload_size", 0))
        response_size = float(payload.get("response_size", 0))
        response_time_ms = float(payload.get("response_time_ms", 0.0))
        status_code = int(payload.get("status_code", 200))
        login_freq = float(payload.get("login_frequency_1h", 0))

        # Method One-Hot Encoding
        is_get = 1.0 if http_method == "GET" else 0.0
        is_post = 1.0 if http_method == "POST" else 0.0
        is_put = 1.0 if http_method == "PUT" else 0.0
        is_delete = 1.0 if http_method == "DELETE" else 0.0

        # Features
        endpoint_depth = float(len([segment for segment in endpoint.split("/") if segment]))
        uri_entropy = self.calculate_entropy(endpoint)
        is_error = 1.0 if status_code >= 400 else 0.0
        log_payload_size = float(np.log1p(max(0.0, payload_size)))
        log_response_size = float(np.log1p(max(0.0, response_size)))
        log_response_time = float(np.log1p(max(0.0, response_time_ms)))

        return {
            "is_get": is_get,
            "is_post": is_post,
            "is_put": is_put,
            "is_delete": is_delete,
            "endpoint_depth": endpoint_depth,
            "uri_entropy": uri_entropy,
            "log_payload_size": log_payload_size,
            "log_response_size": log_response_size,
            "log_response_time": log_response_time,
            "is_error": is_error,
            "login_freq_1h": login_freq
        }

    def to_vector(self, features_dict: Dict[str, float]) -> np.ndarray:
        keys = [
            "is_get", "is_post", "is_put", "is_delete",
            "endpoint_depth", "uri_entropy", "log_payload_size",
            "log_response_size", "log_response_time", "is_error", "login_freq_1h"
        ]
        return np.array([features_dict.get(k, 0.0) for k in keys], dtype=np.float64).reshape(1, -1)
