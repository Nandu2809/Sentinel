import numpy as np
from sklearn.cluster import KMeans
from typing import Tuple

class ClusterAnomalyDetector:
    """
    Cluster-based anomaly detector using K-Means distance thresholds.
    """
    def __init__(self, n_clusters: int = 3, random_state: int = 42):
        self.model = KMeans(n_clusters=n_clusters, random_state=random_state, n_init=10)
        self.max_distance = 1.0
        self.is_fitted = False

    def train(self, X: np.ndarray):
        self.model.fit(X)
        distances = np.min(self.model.transform(X), axis=1)
        self.max_distance = float(np.percentile(distances, 95)) + 1e-5
        self.is_fitted = True

    def predict(self, X: np.ndarray) -> Tuple[float, bool]:
        if not self.is_fitted:
            return 10.0, False

        distances = self.model.transform(X)[0]
        min_dist = float(np.min(distances))
        normalized_score = max(0.0, min(100.0, (min_dist / self.max_distance) * 50.0))
        is_anomaly = min_dist > self.max_distance
        return float(normalized_score), is_anomaly
