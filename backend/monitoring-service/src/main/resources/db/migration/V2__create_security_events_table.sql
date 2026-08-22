CREATE TABLE security_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    user_id UUID,
    email VARCHAR(320),
    ip_address VARCHAR(64),
    user_agent VARCHAR(512),
    outcome VARCHAR(30) NOT NULL,
    message VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_security_events_event_id ON security_events(event_id);
CREATE INDEX idx_security_events_user_id ON security_events(user_id);
CREATE INDEX idx_security_events_email ON security_events(email);
CREATE INDEX idx_security_events_created_at ON security_events(created_at DESC);
