ALTER TABLE alerts
    ADD COLUMN IF NOT EXISTS event_id UUID,
    ADD COLUMN IF NOT EXISTS user_id UUID,
    ADD COLUMN IF NOT EXISTS alert_type VARCHAR(80),
    ADD COLUMN IF NOT EXISTS message VARCHAR(512),
    ALTER COLUMN alert_code DROP NOT NULL,
    ALTER COLUMN title DROP NOT NULL,
    ALTER COLUMN threat_type DROP NOT NULL,
    ALTER COLUMN source_service DROP NOT NULL,
    ALTER COLUMN updated_at DROP NOT NULL;

CREATE INDEX IF NOT EXISTS idx_alerts_event_id ON alerts(event_id);
CREATE INDEX IF NOT EXISTS idx_alerts_user_id ON alerts(user_id);
