-- Añadir columna de moneda a las propuestas con valor por defecto USD
ALTER TABLE lawyer_proposals ADD COLUMN currency VARCHAR(10) DEFAULT 'USD';
