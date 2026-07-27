import uuid
import json
from datetime import datetime
from typing import Dict, Any
from app.features.extractor import FeatureExtractor
from app.explainability.explainer import AnomalyExplainer
from app.models.model_registry import ModelRegistry
from app.training.trainer import ModelTrainer
from app.database.db import get_db_connection

class AnomalyPredictor:
    """
    Online inference engine executing ML model ensemble, score normalization, and XAI synthesis.
    """
    def __init__(self):
        self.extractor = FeatureExtractor()
        self.explainer = AnomalyExplainer()
        self.registry = ModelRegistry()

        # Load models or auto-train baseline if missing
        self.pipeline = self.registry.load_artifact("pipeline")
        self.iso_forest = self.registry.load_artifact("isolation_forest")
        self.oc_svm = self.registry.load_artifact("one_class_svm")
        self.cluster = self.registry.load_artifact("clustering")

        if not self.iso_forest or not self.oc_svm:
            trainer = ModelTrainer()
            trainer.train_pipeline(version="v1.0")
            self.pipeline = self.registry.load_artifact("pipeline")
            self.iso_forest = self.registry.load_artifact("isolation_forest")
            self.oc_svm = self.registry.load_artifact("one_class_svm")
            self.cluster = self.registry.load_artifact("clustering")

    def predict(self, payload: Dict[str, Any]) -> Dict[str, Any]:
        feat_dict = self.extractor.extract_features(payload)
        vector = self.extractor.to_vector(feat_dict)

        if self.pipeline and hasattr(self.pipeline, "transform"):
            scaled_vector = self.pipeline.transform(vector)
        else:
            scaled_vector = vector

        iso_score, iso_flag = self.iso_forest.predict(scaled_vector) if self.iso_forest else (10.0, False)
        svm_score, svm_flag = self.oc_svm.predict(scaled_vector) if self.oc_svm else (10.0, False)
        cls_score, cls_flag = self.cluster.predict(scaled_vector) if self.cluster else (10.0, False)

        # Weighted Ensemble Anomaly Score (0-100)
        ensemble_score = (iso_score * 0.45) + (svm_score * 0.35) + (cls_score * 0.20)
        ensemble_score = max(0.0, min(100.0, ensemble_score))

        # Classification buckets
        if ensemble_score <= 20.0:
            classification = "NORMAL"
        elif ensemble_score <= 50.0:
            classification = "SUSPICIOUS"
        elif ensemble_score <= 80.0:
            classification = "HIGH_RISK"
        else:
            classification = "CRITICAL_ANOMALY"

        xai = self.explainer.explain(feat_dict, ensemble_score)

        result_id = f"anom-{uuid.uuid4().hex[:10]}"
        now = datetime.utcnow().isoformat()

        # Save to DB
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO anomaly_results
            (id, request_id, endpoint, anomaly_score, classification, reason, confidence, details_json, detected_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            result_id,
            payload.get("request_id", result_id),
            payload.get("endpoint", "/"),
            round(ensemble_score, 2),
            classification,
            xai["primary_reason"],
            xai["confidence_score"],
            json.dumps({"features": feat_dict, "xai": xai, "models": {"iso_forest": iso_score, "svm": svm_score, "cluster": cls_score}}),
            now
        ))
        conn.commit()
        conn.close()

        return {
            "id": result_id,
            "request_id": payload.get("request_id", result_id),
            "endpoint": payload.get("endpoint", "/"),
            "anomaly_score": round(ensemble_score, 2),
            "classification": classification,
            "reason": xai["primary_reason"],
            "confidence": xai["confidence_score"],
            "details": {
                "features": feat_dict,
                "xai": xai,
                "model_scores": {
                    "isolation_forest": round(iso_score, 2),
                    "one_class_svm": round(svm_score, 2),
                    "clustering": round(cls_score, 2)
                }
            },
            "detected_at": now
        }
