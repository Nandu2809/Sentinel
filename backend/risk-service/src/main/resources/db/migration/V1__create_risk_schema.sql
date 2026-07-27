CREATE TABLE risk_profiles (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(120) NOT NULL,
    current_risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    max_risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    risk_classification VARCHAR(30) NOT NULL,
    threat_count BIGINT NOT NULL DEFAULT 0,
    incident_count BIGINT NOT NULL DEFAULT 0,
    risk_trend VARCHAR(30) NOT NULL DEFAULT 'STABLE',
    last_updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_risk_profile_entity UNIQUE (entity_type, entity_id)
);

CREATE TABLE risk_scores (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(120) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    classification VARCHAR(30) NOT NULL,
    factors_json TEXT,
    calculated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE risk_history (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(120) NOT NULL,
    previous_score DOUBLE PRECISION NOT NULL,
    new_score DOUBLE PRECISION NOT NULL,
    reason VARCHAR(255),
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE TABLE risk_rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    weight DOUBLE PRECISION NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE endpoint_risk (
    id UUID PRIMARY KEY,
    endpoint VARCHAR(512) NOT NULL UNIQUE,
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    is_critical_asset BOOLEAN NOT NULL DEFAULT FALSE,
    request_count BIGINT NOT NULL DEFAULT 0,
    exploit_attempt_count BIGINT NOT NULL DEFAULT 0,
    last_assessed_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_risk (
    id UUID PRIMARY KEY,
    user_id VARCHAR(80) NOT NULL UNIQUE,
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    auth_failure_count BIGINT NOT NULL DEFAULT 0,
    privilege_level VARCHAR(30) NOT NULL DEFAULT 'STANDARD',
    last_assessed_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE ip_risk (
    id UUID PRIMARY KEY,
    client_ip VARCHAR(64) NOT NULL UNIQUE,
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    reputation_status VARCHAR(30) NOT NULL DEFAULT 'NEUTRAL',
    country_code VARCHAR(10) DEFAULT 'UNKNOWN',
    is_known_bot BOOLEAN NOT NULL DEFAULT FALSE,
    last_assessed_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE organization_security_score (
    id UUID PRIMARY KEY,
    security_score DOUBLE PRECISION NOT NULL DEFAULT 100.0,
    threat_heat_index DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    active_critical_incidents BIGINT NOT NULL DEFAULT 0,
    total_entities_at_risk BIGINT NOT NULL DEFAULT 0,
    calculated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE risk_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(80) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(120) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    details_json TEXT,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_risk_profiles_score ON risk_profiles(current_risk_score DESC);
CREATE INDEX idx_risk_scores_calculated_at ON risk_scores(calculated_at DESC);
CREATE INDEX idx_risk_history_entity ON risk_history(entity_type, entity_id);
CREATE INDEX idx_endpoint_risk_score ON endpoint_risk(risk_score DESC);
CREATE INDEX idx_user_risk_score ON user_risk(risk_score DESC);
CREATE INDEX idx_ip_risk_score ON ip_risk(risk_score DESC);
