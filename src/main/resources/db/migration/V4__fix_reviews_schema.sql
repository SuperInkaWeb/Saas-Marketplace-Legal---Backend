-- Migration to fix missing columns in reviews table
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS public_id UUID UNIQUE DEFAULT gen_random_uuid();
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS is_anonymous BOOLEAN DEFAULT FALSE;
