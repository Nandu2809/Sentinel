CREATE TABLE apis (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    path_pattern VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    rate_limit_per_min INTEGER NOT NULL DEFAULT 100,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE api_requests (
    id UUID PRIMARY KEY,
    api_id UUID REFERENCES apis(id) ON DELETE SET NULL,
    request_id VARCHAR(80) NOT NULL,
    correlation_id VARCHAR(80) NOT NULL,
    client_ip VARCHAR(64) NOT NULL,
    user_agent VARCHAR(512),
    http_method VARCHAR(10) NOT NULL,
    uri VARCHAR(512) NOT NULL,
    status_code INTEGER NOT NULL,
    latency_ms BIGINT NOT NULL,
    request_size BIGINT NOT NULL DEFAULT 0,
    response_size BIGINT NOT NULL DEFAULT 0,
    user_id VARCHAR(80),
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE TABLE api_metrics (
    id UUID PRIMARY KEY,
    api_id UUID REFERENCES apis(id) ON DELETE CASCADE,
    time_bucket TIMESTAMPTZ NOT NULL,
    request_count BIGINT NOT NULL DEFAULT 0,
    error_count BIGINT NOT NULL DEFAULT 0,
    avg_latency_ms DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    p99_latency_ms DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE api_health (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    response_time_ms BIGINT NOT NULL,
    last_checked_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE api_error_logs (
    id UUID PRIMARY KEY,
    request_id VARCHAR(80) NOT NULL,
    api_id UUID REFERENCES apis(id) ON DELETE SET NULL,
    status_code INTEGER NOT NULL,
    error_message VARCHAR(512) NOT NULL,
    stack_trace TEXT,
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_user_id UUID,
    event_type VARCHAR(80) NOT NULL,
    outcome VARCHAR(30) NOT NULL,
    ip_address VARCHAR(64),
    user_agent VARCHAR(512),
    target_type VARCHAR(80),
    target_id VARCHAR(120),
    message VARCHAR(512),
    metadata_json TEXT,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_api_requests_timestamp ON api_requests(timestamp DESC);
CREATE INDEX idx_api_requests_api_id ON api_requests(api_id);
CREATE INDEX idx_api_requests_user_id ON api_requests(user_id);
CREATE INDEX idx_api_requests_client_ip ON api_requests(client_ip);
CREATE INDEX idx_api_requests_status_code ON api_requests(status_code);
CREATE INDEX idx_api_metrics_time_bucket ON api_metrics(api_id, time_bucket DESC);
