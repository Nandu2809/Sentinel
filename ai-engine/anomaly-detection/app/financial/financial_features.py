"""
Sentinel Phase 6C — Financial Feature Extractor Pipeline

Provides pure, clean feature extraction interface producing:
1. BASELINE FEATURES: Transaction-level & directly observable telemetry only.
2. ENHANCED FEATURES: Baseline features + In-Memory Relationship Graph features.

Ensures zero target label leakage (isFraud/fraudScenario excluded from feature vectors).
"""

import math

class FinancialFeatureExtractor:
    """Feature Extractor for Financial Risk Transactions."""

    def __init__(self):
        pass

    def extract_baseline_features(self, event):
        """
        Extract baseline transaction-level and directly observable features.
        Excludes all graph / entity topology metrics.
        """
        amount = float(event.get("amount", 0.0))
        account_age = int(event.get("accountAgeDays", 0))
        velocity_1h = int(event.get("velocity1h", 0))
        failed_tx_24h = int(event.get("failedTxCount24h", 0))
        shared_dev = int(event.get("sharedDeviceAccountCount", 1))
        shared_ip = int(event.get("sharedIpAccountCount", 1))

        # Safe transformations
        log_amount = round(math.log1p(max(0.0, amount)), 4)
        is_new_account = 1 if account_age <= 7 else 0
        high_velocity = 1 if velocity_1h >= 5 else 0

        return {
            "amount": amount,
            "logAmount": log_amount,
            "accountAgeDays": account_age,
            "velocity1h": velocity_1h,
            "failedTxCount24h": failed_tx_24h,
            "sharedDeviceAccountCount": shared_dev,
            "sharedIpAccountCount": shared_ip,
            "isNewAccountFlag": is_new_account,
            "highVelocityFlag": high_velocity
        }

    def extract_enhanced_features(self, event, graph):
        """
        Extract enhanced features: Baseline features + Graph Relationship features.
        Graph metrics are calculated using temporal state up to the event timestamp.
        """
        baseline = self.extract_baseline_features(event)
        graph_metrics = graph.get_relationship_metrics(event)

        # Merge baseline and graph relationship features
        enhanced = {**baseline, **graph_metrics}

        # Sanitize any potential NaN or Infinite values
        for k, v in enhanced.items():
            if isinstance(v, float) and (math.isnan(v) or math.isinf(v)):
                enhanced[k] = 0.0

        return enhanced
