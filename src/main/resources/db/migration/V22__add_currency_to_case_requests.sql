-- Añadir columna de moneda a los casos con valor por defecto USD
ALTER TABLE case_requests ADD COLUMN currency VARCHAR(10) DEFAULT 'USD';
