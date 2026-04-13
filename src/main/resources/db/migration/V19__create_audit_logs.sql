-- =====================================================================
-- V19: Audit Log - Tracks critical changes on matters
-- =====================================================================

-- Drop any pre-existing audit_logs table with incompatible schema
DROP TABLE IF EXISTS audit_logs;

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    
    -- What changed
    entity_type VARCHAR(50) NOT NULL,   -- e.g., 'MATTER'
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,         -- e.g., 'STATUS_CHANGE', 'CREATED', 'PARTICIPANT_ADDED'

    -- Who changed it
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    
    -- Details
    old_value VARCHAR(500),
    new_value VARCHAR(500),
    description TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at);
