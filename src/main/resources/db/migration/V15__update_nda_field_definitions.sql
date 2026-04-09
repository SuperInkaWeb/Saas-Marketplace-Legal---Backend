-- Migración V15 (Restaurada para validación de Flyway)
UPDATE document_templates 
SET field_definitions = '[
    {"name": "EMPRESA_A", "label": "Nombre de la Empresa (Parte A)", "type": "TEXT", "required": true, "placeholder": "Ej: Innova Tech S.A.C."},
    {"name": "EMPRESA_B", "label": "Nombre del Receptor (Parte B)", "type": "TEXT", "required": true, "placeholder": "Ej: Juan Pérez o Empresa Destino"},
    {"name": "MESES_VIGENCIA", "label": "Meses de Vigencia", "type": "NUMBER", "required": true, "placeholder": "Ej: 12 o 24"},
    {"name": "MONTO_PENALIDAD", "label": "Monto de Penalidad (USD)", "type": "NUMBER", "required": true, "placeholder": "Ej: 5000"},
    {"name": "FECHA_RESOLUCION_CONFLICTOS", "label": "Fecha resolución de conflictos", "type": "DATE", "required": true}
]'
WHERE code = 'NDA_STANDARD';

UPDATE document_templates
SET required_fields = 'EMPRESA_A,EMPRESA_B,MESES_VIGENCIA,MONTO_PENALIDAD,FECHA_RESOLUCION_CONFLICTOS'
WHERE code = 'NDA_STANDARD';
