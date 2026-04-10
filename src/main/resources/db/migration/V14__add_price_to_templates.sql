ALTER TABLE document_templates
ADD COLUMN IF NOT EXISTS price DECIMAL(10,2) DEFAULT 0.00;

UPDATE document_templates 
SET price = 45.00 
WHERE code = 'RENT_PROPERTY';

UPDATE document_templates 
SET price = 75.00 
WHERE code = 'LABOR_MODALIDAD_01';

UPDATE document_templates 
SET price = 35.00 
WHERE code = 'NDA_BILATERAL_01';

UPDATE document_templates 
SET price = 120.00 
WHERE code = 'MINUTA_EIRL_01';
