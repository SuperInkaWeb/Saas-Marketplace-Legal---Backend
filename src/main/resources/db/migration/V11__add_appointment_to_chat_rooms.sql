-- =============================================
-- V11: Adapt Chat Rooms for Appointments
-- =============================================

-- Drop the old constraint making case_request_id NOT NULL
-- (PostgreSQL approach for altering not null columns)
ALTER TABLE chat_rooms ALTER COLUMN case_request_id DROP NOT NULL;

-- Drop the existing UNIQUE constraint if it exists automatically 
-- Wait, we added UNIQUE(case_request_id), so we should keep it UNIQUE but allow NULLs.
-- PostgreSQL UNIQUE constraint ignores NULLs by default, but let's be careful.

-- Add appointment reference
ALTER TABLE chat_rooms ADD COLUMN appointment_id BIGINT REFERENCES appointments(id);

-- Enforce mutually exclusive relationship (Either Case XOR Appointment)
ALTER TABLE chat_rooms
ADD CONSTRAINT chk_case_or_appt 
CHECK (
    (case_request_id IS NOT NULL AND appointment_id IS NULL) OR 
    (case_request_id IS NULL AND appointment_id IS NOT NULL)
);

-- Add unique constraint for appointments to avoid duplicate chat rooms
ALTER TABLE chat_rooms ADD CONSTRAINT uq_appointment_chat UNIQUE (appointment_id);
