-- Add ADMIN role
INSERT INTO roles (name, description) VALUES ('ADMIN', 'Administrador de la plataforma')
ON CONFLICT (name) DO NOTHING;

-- Add missing columns to notifications table
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS title VARCHAR(255);
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS public_id UUID UNIQUE DEFAULT gen_random_uuid();
