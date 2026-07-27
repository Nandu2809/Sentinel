import numpy as np
from sklearn.ensemble import IsolationForest
from typing import Tuple

class IsolationForestDetector:
    """
    Isolation Forest algorithm for identifying multivariate API traffic outliers.
    """
    def __init__(self, contamination: float = 0.05, random_state: int = 42):
        self.model = IsolationForest(
            n_estimators=100,
            contamination=contamination,
            random_state=random_state
        )
        self.is_fitted = False

    def train(self, X: np.ndarray):
        self.model.fit(X)
        self.is_fitted = True

    def predict(self, X: np.ndarray) -> Tuple[float, bool]:
        """
        Returns normalized anomaly score (0.0 to 100.0) and boolean is_anomaly flag.
        """
        if not self.is_fitted:
            # Fallback for unfitted model: score based on heuristic bounds
            return 10.0, False

        raw_score = self.model.score_samples(X)[0]  # Values range from ~ -1.0 to 0.5
        # Convert raw isolation score to 0-100 scale (lower raw score -> higher anomaly)
        # raw_score ~ 0.5 -> normal (0%), raw_score ~ -0.8 -> anomaly (100%)
        normalized_score = max(0.0, min(100.0, (0.5 - raw_score) * 80.0))
        is_anomaly = self.model.predict(X)[0] == -1
        return float(normalized_score), is_anomaly
