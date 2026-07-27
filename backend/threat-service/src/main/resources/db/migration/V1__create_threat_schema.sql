CREATE TABLE threat_rules (
    id UUID PRIMARY KEY,
    rule_code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    threat_type VARCHAR(80) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    threshold INTEGER NOT NULL DEFAULT 1,
    recommendation VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE threat_events (
    id UUID PRIMARY KEY,
    threat_code VARCHAR(80) NOT NULL UNIQUE,
    correlation_id VARCHAR(80) NOT NULL,
    request_id VARCHAR(80) NOT NULL,
    user_id VARCHAR(80),
    client_ip VARCHAR(64) NOT NULL,
    endpoint VARCHAR(512) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    threat_type VARCHAR(80) NOT NULL,
    matched_rule_id UUID REFERENCES threat_rules(id) ON DELETE SET NULL,
    severity VARCHAR(30) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    recommendation VARCHAR(512),
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE detections (
    id UUID PRIMARY KEY,
    threat_event_id UUID NOT NULL REFERENCES threat_events(id) ON DELETE CASCADE,
    rule_id UUID REFERENCES threat_rules(id) ON DELETE SET NULL,
    detector_name VARCHAR(100) NOT NULL,
    matched_pattern VARCHAR(255),
    raw_payload_sample TEXT,
    detected_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE attack_patterns (
    id UUID PRIMARY KEY,
    rule_code VARCHAR(80) NOT NULL,
    pattern_type VARCHAR(50) NOT NULL,
    regex_pattern VARCHAR(512) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE incident_history (
    id UUID PRIMARY KEY,
    incident_code VARCHAR(80) NOT NULL UNIQUE,
    threat_type VARCHAR(80) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    affected_endpoint VARCHAR(512) NOT NULL,
    affected_user VARCHAR(80),
    evidence_json TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    mitigation_recommendation VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE risk_scores (
    id UUID PRIMARY KEY,
    client_ip VARCHAR(64) NOT NULL,
    user_id VARCHAR(80),
    endpoint VARCHAR(512) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    calculated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE security_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(80) NOT NULL,
    source_service VARCHAR(80) NOT NULL,
    details_json TEXT,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE rule_execution_logs (
    id UUID PRIMARY KEY,
    rule_code VARCHAR(80) NOT NULL,
    execution_time_ms BIGINT NOT NULL,
    matched BOOLEAN NOT NULL,
    evaluated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_threat_events_created_at ON threat_events(created_at DESC);
CREATE INDEX idx_threat_events_client_ip ON threat_events(client_ip);
CREATE INDEX idx_threat_events_severity ON threat_events(severity);
CREATE INDEX idx_threat_events_threat_type ON threat_events(threat_type);
CREATE INDEX idx_incident_status ON incident_history(status);
CREATE INDEX idx_incident_severity ON incident_history(severity);
CREATE INDEX idx_risk_scores_client_ip ON risk_scores(client_ip);
