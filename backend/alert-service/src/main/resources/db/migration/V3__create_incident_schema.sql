CREATE TABLE incidents (
    id UUID PRIMARY KEY,
    incident_number VARCHAR(80) NOT NULL UNIQUE,
    alert_id UUID,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    assigned_analyst VARCHAR(80),
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    ai_confidence DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    affected_user VARCHAR(80),
    affected_ip VARCHAR(64),
    affected_device VARCHAR(120),
    evidence_json TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    resolved_at TIMESTAMPTZ,
    CONSTRAINT fk_incidents_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE SET NULL
);

CREATE TABLE incident_timeline_events (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    source VARCHAR(80) NOT NULL,
    summary VARCHAR(512) NOT NULL,
    details_json TEXT,
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_incident_timeline_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
);

CREATE TABLE incident_actions (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    performed_by VARCHAR(80) NOT NULL,
    target VARCHAR(255),
    details TEXT,
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_incident_actions_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
);

CREATE TABLE incident_notes (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    author VARCHAR(80) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_incident_notes_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
);

CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_severity ON incidents(severity);
CREATE INDEX idx_incidents_alert_id ON incidents(alert_id);
CREATE INDEX idx_incidents_created_at ON incidents(created_at DESC);
CREATE INDEX idx_incidents_affected_user ON incidents(affected_user);
CREATE INDEX idx_incidents_affected_ip ON incidents(affected_ip);
CREATE INDEX idx_incident_timeline_incident_id ON incident_timeline_events(incident_id);
CREATE INDEX idx_incident_actions_incident_id ON incident_actions(incident_id);
CREATE INDEX idx_incident_notes_incident_id ON incident_notes(incident_id);
