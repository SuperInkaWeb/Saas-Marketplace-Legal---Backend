-- Migration to add is_active column to specialties table
ALTER TABLE specialties ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
