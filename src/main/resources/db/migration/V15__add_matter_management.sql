-- =====================================================================
-- V15: Archietcture for Phase 1 of Legal ERP - Matter Management
-- =====================================================================

-- Matter Status Enum-like representation via Check Constraint or simple VARCHAR
CREATE TABLE IF NOT EXISTS matters (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    number VARCHAR(100) UNIQUE, -- Exp identifier (e.g., 2026-0001)
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    jurisdiction VARCHAR(100),
    lawyer_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    client_id BIGINT REFERENCES client_profiles(id) ON DELETE CASCADE,
    case_request_id BIGINT REFERENCES case_requests(id) ON DELETE SET NULL,
    start_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    estimated_end_date TIMESTAMP WITH TIME ZONE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_matters_lawyer ON matters(lawyer_id);
CREATE INDEX IF NOT EXISTS idx_matters_client ON matters(client_id);
CREATE INDEX IF NOT EXISTS idx_matters_status ON matters(status);

-- Update documents table to link to Matters
ALTER TABLE documents
ADD COLUMN IF NOT EXISTS matter_id BIGINT REFERENCES matters(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_documents_matter ON documents(matter_id);
