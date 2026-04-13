-- =====================================================================
-- V17: Legal ERP Phase 2 - Timeline (Events) and Task Management
-- =====================================================================

-- Matter Events (Actuaciones / Historial)
CREATE TABLE IF NOT EXISTS matter_events (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    matter_id BIGINT NOT NULL REFERENCES matters(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_type VARCHAR(50) NOT NULL, -- e.g., HEARING, FILING, NOTIFICATION, NOTE
    event_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    document_id BIGINT REFERENCES documents(id) ON DELETE SET NULL,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_matter_events_matter ON matter_events(matter_id);
CREATE INDEX IF NOT EXISTS idx_matter_events_date ON matter_events(event_date);

-- Matter Tasks (Tareas pendientes)
CREATE TABLE IF NOT EXISTS matter_tasks (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    matter_id BIGINT NOT NULL REFERENCES matters(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO', -- e.g., TODO, IN_PROGRESS, DONE
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_matter_tasks_matter ON matter_tasks(matter_id);
CREATE INDEX IF NOT EXISTS idx_matter_tasks_status ON matter_tasks(status);
CREATE INDEX IF NOT EXISTS idx_matter_tasks_due_date ON matter_tasks(due_date);
