from typing import Dict, Any, List

class AnomalyExplainer:
    """
    Computes feature deviations and generates plain-English Explainable AI (XAI) insights for security analysts.
    """

    # Population baselines for normal API traffic
    BASELINES = {
        "uri_entropy": 3.0,
        "log_payload_size": 4.5,    # ~90 bytes
        "log_response_size": 6.0,   # ~400 bytes
        "log_response_time": 3.5,   # ~33 ms
        "login_freq_1h": 1.0,
        "endpoint_depth": 3.0
    }

    def explain(self, features: Dict[str, float], ensemble_score: float) -> Dict[str, Any]:
        importance: Dict[str, float] = {}
        reasons: List[str] = []
        max_dev = 0.0

        for key, value in features.items():
            if key in self.BASELINES:
                base = self.BASELINES[key]
                diff = abs(value - base)
                dev_pct = (diff / base) * 100.0 if base > 0 else 0.0
                importance[key] = round(dev_pct, 2)
                if dev_pct > max_dev:
                    max_dev = dev_pct

                # Specific plain-English reason generation
                if key == "uri_entropy" and value > 4.5:
                    reasons.append(f"High URI string entropy ({value:.2f}), suggesting obfuscated payload injection.")
                elif key == "log_payload_size" and value > 8.0: # > 3000 bytes
                    reasons.append(f"Unusually large request payload size (Log Value: {value:.2f}).")
                elif key == "log_response_time" and value > 6.0: # > 400 ms
                    reasons.append(f"High server execution latency ({math_exp_ms(value):.1f} ms).")
                elif key == "login_freq_1h" and value >= 5.0:
                    reasons.append(f"Abnormal authentication velocity ({int(value)} logins/hr).")
                elif key == "is_error" and value > 0:
                    reasons.append("Request resulted in an HTTP 4xx/5xx client/server error.")

        if not reasons:
            if ensemble_score > 75.0:
                reasons.append("Multivariate statistical deviation across combined request metrics.")
            else:
                reasons.append("API traffic behavior aligns within standard operating parameters.")

        primary_reason = " ".join(reasons)
        confidence = min(0.99, max(0.50, ensemble_score / 100.0))

        return {
            "primary_reason": primary_reason,
            "feature_importance": importance,
            "max_deviation_pct": round(max_dev, 2),
            "confidence_score": round(confidence, 4)
        }

def math_exp_ms(log_val: float) -> float:
    import numpy as np
    return float(np.expm1(log_val))
