CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    alert_code VARCHAR(80) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    threat_type VARCHAR(80) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    source_service VARCHAR(80) NOT NULL,
    affected_api VARCHAR(512),
    affected_user VARCHAR(80),
    affected_ip VARCHAR(64),
    correlation_id VARCHAR(120),
    evidence_json TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    assigned_analyst VARCHAR(80),
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    resolution_notes TEXT
);

CREATE TABLE alert_events (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    source_service VARCHAR(80) NOT NULL,
    payload_json TEXT,
    received_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_alert_events_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE alert_assignments (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    previous_analyst VARCHAR(80),
    new_analyst VARCHAR(80) NOT NULL,
    assigned_by VARCHAR(80) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_alert_assign_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE alert_comments (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    analyst VARCHAR(80) NOT NULL,
    comment TEXT NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_alert_comments_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE alert_history (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    previous_status VARCHAR(30) NOT NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by VARCHAR(80) NOT NULL,
    reason VARCHAR(255),
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_alert_history_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE notification_logs (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    channel VARCHAR(30) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    error_message VARCHAR(512),
    sent_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_notif_logs_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE notification_preferences (
    id UUID PRIMARY KEY,
    user_id VARCHAR(80) NOT NULL UNIQUE,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    websocket_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    webhook_url VARCHAR(512),
    min_severity VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE escalation_rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    severity VARCHAR(30) NOT NULL,
    unacknowledged_timeout_minutes INT NOT NULL DEFAULT 15,
    escalate_to_role VARCHAR(80) NOT NULL DEFAULT 'SOC_LEAD',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_created_at ON alerts(created_at DESC);
CREATE INDEX idx_alerts_correlation ON alerts(correlation_id);
CREATE INDEX idx_alerts_analyst ON alerts(assigned_analyst);
