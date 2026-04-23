-- Aumentar la longitud de las columnas para evitar errores de truncamiento con tipos MIME largos
ALTER TABLE documents ALTER COLUMN file_type TYPE VARCHAR(255);
ALTER TABLE documents ALTER COLUMN signature_status TYPE VARCHAR(100);
