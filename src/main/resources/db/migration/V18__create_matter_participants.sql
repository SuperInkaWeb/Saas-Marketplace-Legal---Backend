-- =====================================================================
-- V18: Legal ERP Phase 3 - Participant Management
-- Supports: Opposing counsel, Judges, Courts, Witnesses, Experts
-- =====================================================================

CREATE TABLE IF NOT EXISTS matter_participants (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    matter_id BIGINT NOT NULL REFERENCES matters(id) ON DELETE CASCADE,
    
    -- Identity
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- OPPOSING_COUNSEL, OPPOSING_PARTY, JUDGE, COURT, WITNESS, EXPERT, OTHER
    
    -- Contact
    email VARCHAR(255),
    phone VARCHAR(30),
    
    -- Professional context
    firm_or_institution VARCHAR(255), -- Law firm name, Court name, Institution
    professional_id VARCHAR(100),     -- Bar number, Badge number, etc.
    
    -- Notes
    notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_matter_participants_matter ON matter_participants(matter_id);
CREATE INDEX IF NOT EXISTS idx_matter_participants_role ON matter_participants(role);
