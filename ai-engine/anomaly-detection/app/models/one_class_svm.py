import numpy as np
from sklearn.svm import OneClassSVM
from typing import Tuple

class OneClassSVMDetector:
    """
    One-Class Support Vector Machine for API behavioral boundary verification.
    """
    def __init__(self, nu: float = 0.05, kernel: str = 'rbf', gamma: str = 'scale'):
        self.model = OneClassSVM(nu=nu, kernel=kernel, gamma=gamma)
        self.is_fitted = False

    def train(self, X: np.ndarray):
        self.model.fit(X)
        self.is_fitted = True

    def predict(self, X: np.ndarray) -> Tuple[float, bool]:
        if not self.is_fitted:
            return 10.0, False

        decision_dist = self.model.decision_function(X)[0]
        # Negative decision_function value indicates point is outside the boundary
        is_anomaly = self.model.predict(X)[0] == -1
        normalized_score = max(0.0, min(100.0, (1.0 - decision_dist) * 50.0))
        return float(normalized_score), is_anomaly
