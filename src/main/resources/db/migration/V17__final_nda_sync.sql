-- Migración V17: Sincronización Forzada de Contenido y Metadatos para NDA
-- Esta migración sobreescribe el contenido y las definiciones para asegurar compatibilidad total

UPDATE document_templates 
SET content = '# CONTRATO DE CONFIDENCIALIDAD (NDA)

Entre {{EMPRESA_A}} (en adelante, "La Empresa") y {{EMPRESA_B}} (en adelante, "El Receptor"), se acuerda mutuamente mantener en estricta reserva toda información calificada como confidencial.

## 1. Objeto
El Receptor se compromete a no divulgar secretos comerciales de La Empresa.

## 2. Vigencia
Este acuerdo tiene validez durante {{MESES_VIGENCIA}} meses desde su firma.

## 3. Penalidad
Ante un incumplimiento, se fijará la sanción de {{MONTO_PENALIDAD}} USD.

Para cualquier controversia, revisar con fecha: {{FECHA_RESOLUCION_CONFLICTOS}}.',
field_definitions = '[
    {"name": "EMPRESA_A", "label": "Nombre de la Empresa (Parte A)", "type": "TEXT", "required": true, "placeholder": "Ej: Innova Tech S.A.C."},
    {"name": "EMPRESA_B", "label": "Nombre del Receptor (Parte B)", "type": "TEXT", "required": true, "placeholder": "Ej: Juan Pérez"},
    {"name": "MESES_VIGENCIA", "label": "Meses de Vigencia", "type": "NUMBER", "required": true, "placeholder": "Ej: 12"},
    {"name": "MONTO_PENALIDAD", "label": "Monto de Penalidad (USD)", "type": "NUMBER", "required": true, "placeholder": "Ej: 500"},
    {"name": "FECHA_RESOLUCION_CONFLICTOS", "label": "Fecha resolución de conflictos", "type": "DATE", "required": true}
]',
required_fields = 'EMPRESA_A,EMPRESA_B,MESES_VIGENCIA,MONTO_PENALIDAD,FECHA_RESOLUCION_CONFLICTOS'
WHERE code = 'NDA_STANDARD';
