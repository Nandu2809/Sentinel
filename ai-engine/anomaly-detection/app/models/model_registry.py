import os
import joblib
import json
from datetime import datetime
from typing import Dict, Any, List
from app.config.settings import settings
from app.database.db import get_db_connection

class ModelRegistry:
    """
    Manages loading, saving, and version control for trained ML model artifacts.
    """
    def __init__(self):
        os.makedirs(settings.MODEL_DIR, exist_ok=True)

    def save_artifact(self, name: str, version: str, model_obj: Any, metrics: Dict[str, Any]):
        filepath = os.path.join(settings.MODEL_DIR, f"{name}_{version}.joblib")
        joblib.dump(model_obj, filepath)

        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("""
            INSERT OR REPLACE INTO model_registry (id, name, version, type, status, metrics_json, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, (
            f"{name}-{version}",
            name,
            version,
            model_obj.__class__.__name__,
            "ACTIVE",
            json.dumps(metrics),
            datetime.utcnow().isoformat()
        ))
        conn.commit()
        conn.close()

    def load_artifact(self, name: str, version: str = "latest") -> Any:
        conn = get_db_connection()
        cursor = conn.cursor()
        if version == "latest":
            cursor.execute("SELECT version FROM model_registry WHERE name = ? ORDER BY created_at DESC LIMIT 1", (name,))
            row = cursor.fetchone()
            if not row:
                conn.close()
                return None
            version = row["version"]
        conn.close()

        filepath = os.path.join(settings.MODEL_DIR, f"{name}_{version}.joblib")
        if os.path.exists(filepath):
            return joblib.load(filepath)
        return None

    def list_models(self) -> List[Dict[str, Any]]:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, name, version, type, status, metrics_json, created_at FROM model_registry ORDER BY created_at DESC")
        rows = cursor.fetchall()
        conn.close()

        models_list = []
        for r in rows:
            models_list.append({
                "id": r["id"],
                "name": r["name"],
                "version": r["version"],
                "type": r["type"],
                "status": r["status"],
                "metrics": json.loads(r["metrics_json"]) if r["metrics_json"] else {},
                "created_at": r["created_at"]
            })
        return models_list
