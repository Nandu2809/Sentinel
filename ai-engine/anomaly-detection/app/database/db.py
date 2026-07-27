import sqlite3
import os
from app.config.settings import settings

_db_initialized = False

def get_db_connection():
    global _db_initialized
    os.makedirs(os.path.dirname(settings.DB_PATH), exist_ok=True)
    conn = sqlite3.connect(settings.DB_PATH)
    conn.row_factory = sqlite3.Row
    if not _db_initialized:
        init_db_schema(conn)
        _db_initialized = True
    return conn

def init_db_schema(conn=None):
    close_conn = False
    if conn is None:
        os.makedirs(os.path.dirname(settings.DB_PATH), exist_ok=True)
        conn = sqlite3.connect(settings.DB_PATH)
        conn.row_factory = sqlite3.Row
        close_conn = True

    cursor = conn.cursor()
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS ml_features (
        id TEXT PRIMARY KEY,
        request_id TEXT,
        endpoint TEXT,
        feature_json TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS model_registry (
        id TEXT PRIMARY KEY,
        name TEXT UNIQUE,
        version TEXT,
        type TEXT,
        status TEXT,
        metrics_json TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS training_history (
        id TEXT PRIMARY KEY,
        model_name TEXT,
        samples_count INTEGER,
        metrics_json TEXT,
        trained_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS anomaly_results (
        id TEXT PRIMARY KEY,
        request_id TEXT,
        endpoint TEXT,
        anomaly_score REAL,
        classification TEXT,
        reason TEXT,
        confidence REAL,
        details_json TEXT,
        detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    conn.commit()
    if close_conn:
        conn.close()

def init_db():
    init_db_schema()
