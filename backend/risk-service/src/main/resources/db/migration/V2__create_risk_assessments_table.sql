CREATE TABLE risk_assessments (
    id UUID PRIMARY KEY,
    user_id UUID,
    threat_id UUID,
    risk_score INTEGER NOT NULL,
    risk_level VARCHAR(30) NOT NULL,
    decision VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_risk_assessments_user_id ON risk_assessments(user_id);
CREATE INDEX idx_risk_assessments_threat_id ON risk_assessments(threat_id);
CREATE INDEX idx_risk_assessments_created_at ON risk_assessments(created_at DESC);
