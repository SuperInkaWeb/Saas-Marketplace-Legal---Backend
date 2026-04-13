-- =====================================================================
-- V16: Add Unregistered Client Name to Matters for Production ERP Flexibility
-- =====================================================================

ALTER TABLE matters 
ADD COLUMN IF NOT EXISTS unregistered_client_name VARCHAR(255);
