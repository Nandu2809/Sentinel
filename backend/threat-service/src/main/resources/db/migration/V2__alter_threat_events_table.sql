ALTER TABLE threat_events 
    ADD COLUMN IF NOT EXISTS event_id UUID,
    ADD COLUMN IF NOT EXISTS description VARCHAR(512),
    ALTER COLUMN threat_code DROP NOT NULL,
    ALTER COLUMN correlation_id DROP NOT NULL,
    ALTER COLUMN request_id DROP NOT NULL,
    ALTER COLUMN client_ip DROP NOT NULL,
    ALTER COLUMN endpoint DROP NOT NULL,
    ALTER COLUMN http_method DROP NOT NULL;

CREATE INDEX IF NOT EXISTS idx_threat_events_event_id ON threat_events(event_id);
