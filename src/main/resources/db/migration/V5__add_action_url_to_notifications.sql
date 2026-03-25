-- Add missing action_url column to notifications table for client-side routing
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS action_url VARCHAR(255);
