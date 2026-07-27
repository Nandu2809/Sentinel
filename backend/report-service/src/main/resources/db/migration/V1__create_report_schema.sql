CREATE TABLE dashboard_snapshots (
    id UUID PRIMARY KEY,
    total_apis_monitored BIGINT NOT NULL DEFAULT 0,
    total_requests BIGINT NOT NULL DEFAULT 0,
    total_threats_detected BIGINT NOT NULL DEFAULT 0,
    active_incidents BIGINT NOT NULL DEFAULT 0,
    critical_alerts BIGINT NOT NULL DEFAULT 0,
    current_security_score DOUBLE PRECISION NOT NULL DEFAULT 100.0,
    risk_level VARCHAR(30) NOT NULL DEFAULT 'LOW',
    captured_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE security_metrics (
    id UUID PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    category VARCHAR(50) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE TABLE threat_statistics (
    id UUID PRIMARY KEY,
    threat_type VARCHAR(80) NOT NULL,
    count BIGINT NOT NULL DEFAULT 0,
    severity VARCHAR(30) NOT NULL,
    last_detected_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE risk_statistics (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    average_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    max_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    high_risk_count BIGINT NOT NULL DEFAULT 0,
    calculated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE alert_statistics (
    id UUID PRIMARY KEY,
    total_alerts BIGINT NOT NULL DEFAULT 0,
    mtta_seconds DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    mttr_seconds DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    resolution_rate DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    calculated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE report_history (
    id UUID PRIMARY KEY,
    report_code VARCHAR(80) NOT NULL UNIQUE,
    report_type VARCHAR(50) NOT NULL,
    format VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    content_json TEXT,
    created_by VARCHAR(80) NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE scheduled_reports (
    id UUID PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL UNIQUE,
    report_type VARCHAR(50) NOT NULL,
    format VARCHAR(20) NOT NULL,
    cron_expression VARCHAR(60) NOT NULL,
    recipients_json TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at TIMESTAMPTZ
);

CREATE INDEX idx_dashboard_captured_at ON dashboard_snapshots(captured_at DESC);
CREATE INDEX idx_security_metrics_category ON security_metrics(category, timestamp DESC);
CREATE INDEX idx_report_history_type ON report_history(report_type);
