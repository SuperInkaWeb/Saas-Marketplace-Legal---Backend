ALTER TABLE document_templates ADD COLUMN field_definitions TEXT;

-- Update existing templates with basic field definitions if possible
UPDATE document_templates 
SET field_definitions = '[
    {"name": "FECHA", "label": "Fecha del Acuerdo", "type": "DATE", "required": true},
    {"name": "PARTE_REVELADORA", "label": "Nombre Parte Reveladora", "type": "TEXT", "required": true},
    {"name": "PARTE_RECEPTORA", "label": "Nombre Parte Receptora", "type": "TEXT", "required": true},
    {"name": "AÑOS_VIGENCIA", "label": "Años de Vigencia", "type": "NUMBER", "required": true}
]'
WHERE code = 'NDA_STANDARD';

UPDATE document_templates 
SET field_definitions = '[
    {"name": "CIUDAD_FIRMA", "label": "Ciudad de Firma", "type": "TEXT", "required": true},
    {"name": "FECHA_FIRMA", "label": "Fecha de Firma", "type": "DATE", "required": true},
    {"name": "NOMBRE_CLIENTE", "label": "Nombre del Cliente", "type": "TEXT", "required": true},
    {"name": "ID_CLIENTE", "label": "Documento Identidad Cliente", "type": "TEXT", "required": true},
    {"name": "NOMBRE_PROVEEDOR", "label": "Nombre del Proveedor", "type": "TEXT", "required": true},
    {"name": "ID_PROVEEDOR", "label": "Documento Identidad Proveedor", "type": "TEXT", "required": true},
    {"name": "DESCRIPCION_SERVICIOS", "label": "Descripción de Servicios", "type": "TEXT", "required": true},
    {"name": "PRECIO_TOTAL", "label": "Monto Total", "type": "NUMBER", "required": true},
    {"name": "MONEDA", "label": "Moneda", "type": "SELECT", "required": true, "options": ["USD", "EUR", "MXN", "PEN"]},
    {"name": "PLAZO_PAGO", "label": "Plazo de Pago", "type": "TEXT", "required": true},
    {"name": "FECHA_FIN", "label": "Fecha de Finalización", "type": "DATE", "required": true}
]'
WHERE code = 'SERVICE_AGREEMENT';
