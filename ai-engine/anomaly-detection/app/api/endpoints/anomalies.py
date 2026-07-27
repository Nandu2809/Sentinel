import json
from fastapi import APIRouter, HTTPException
from app.database.db import get_db_connection

router = APIRouter()

@router.get("/anomalies")
def get_anomalies(limit: int = 50):
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("""
        SELECT id, request_id, endpoint, anomaly_score, classification, reason, confidence, details_json, detected_at
        FROM anomaly_results
        ORDER BY detected_at DESC
        LIMIT ?
    """, (limit,))
    rows = cursor.fetchall()
    conn.close()

    results = []
    for r in rows:
        results.append({
            "id": r["id"],
            "request_id": r["request_id"],
            "endpoint": r["endpoint"],
            "anomaly_score": r["anomaly_score"],
            "classification": r["classification"],
            "reason": r["reason"],
            "confidence": r["confidence"],
            "details": json.loads(r["details_json"]) if r["details_json"] else {},
            "detected_at": r["detected_at"]
        })
    return {"status": "SUCCESS", "count": len(results), "anomalies": results}

@router.get("/anomalies/{anomaly_id}")
def get_anomaly_by_id(anomaly_id: str):
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("""
        SELECT id, request_id, endpoint, anomaly_score, classification, reason, confidence, details_json, detected_at
        FROM anomaly_results
        WHERE id = ?
    """, (anomaly_id,))
    r = cursor.fetchone()
    conn.close()

    if not r:
        raise HTTPException(status_code=404, detail=f"Anomaly with id {anomaly_id} not found")

    return {
        "status": "SUCCESS",
        "data": {
            "id": r["id"],
            "request_id": r["request_id"],
            "endpoint": r["endpoint"],
            "anomaly_score": r["anomaly_score"],
            "classification": r["classification"],
            "reason": r["reason"],
            "confidence": r["confidence"],
            "details": json.loads(r["details_json"]) if r["details_json"] else {},
            "detected_at": r["detected_at"]
        }
    }
