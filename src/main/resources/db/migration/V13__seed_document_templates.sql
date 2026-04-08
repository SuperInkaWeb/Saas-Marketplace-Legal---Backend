INSERT INTO document_templates (public_id, name, code, jurisdiction, content, required_fields, is_active)
VALUES
(
    gen_random_uuid(),
    'Acuerdo de Confidencialidad (NDA)',
    'NDA_STANDARD',
    'Global / General',
    '# Acuerdo de Confidencialidad (NDA)

Este Acuerdo de Confidencialidad (el "Acuerdo") se celebra en la fecha {{FECHA}}, entre:

**Parte Reveladora:** {{PARTE_REVELADORA}}
**Parte Receptora:** {{PARTE_RECEPTORA}}

## 1. Propósito
La Parte Reveladora tiene la intención de revelar cierta información confidencial y propietaria (la "Información Confidencial") a la Parte Receptora con el fin de explorar una posible relación comercial.

## 2. Definición de Información Confidencial
Por "Información Confidencial" se entenderá toda la información revelada por la Parte Reveladora que se designe como confidencial, o que razonablemente deba entenderse como confidencial dada la naturaleza de la información y las circunstancias de la divulgación.

## 3. Obligaciones de la Parte Receptora
La Parte Receptora deberá:
a) Mantener la Información Confidencial en estricta confidencialidad.
b) No divulgar la Información Confidencial a terceros sin el previo consentimiento por escrito de la Parte Reveladora.
c) Utilizar la Información Confidencial únicamente para el Propósito establecido en la Sección 1.

## 4. Período de Confidencialidad
Las obligaciones de la Parte Receptora sobrevivirán a la terminación de este Acuerdo por un período de {{AÑOS_VIGENCIA}} años.

**Firma Parte Reveladora:** ___________________________
**Firma Parte Receptora:** ___________________________',
    'FECHA,PARTE_REVELADORA,PARTE_RECEPTORA,AÑOS_VIGENCIA',
    true
),
(
    gen_random_uuid(),
    'Contrato de Prestación de Servicios',
    'SERVICE_AGREEMENT',
    'España / General',
    '# Contrato de Prestación de Servicios

En {{CIUDAD_FIRMA}}, a {{FECHA_FIRMA}},

**DE UNA PARTE:** {{NOMBRE_CLIENTE}}, con documento de identidad {{ID_CLIENTE}}, en adelante el "Cliente".
**DE OTRA PARTE:** {{NOMBRE_PROVEEDOR}}, con documento de identidad {{ID_PROVEEDOR}}, en adelante el "Proveedor".

Ambas partes acuerdan lo siguiente:

## 1. Objeto del Contrato
El Proveedor se compromete a prestar los siguientes servicios al Cliente: {{DESCRIPCION_SERVICIOS}}.

## 2. Contraprestación Económica
El Cliente abonará al Proveedor la cantidad de {{PRECIO_TOTAL}} {{MONEDA}} en el siguiente plazo: {{PLAZO_PAGO}}.

## 3. Duración
El presente contrato entrará en vigor el día de su firma y finalizará el {{FECHA_FIN}}.

## 4. Confidencialidad
El Proveedor se compromete a mantener en secreto toda la información proporcionada por el Cliente.

Leído y conforme por ambas partes:

**El Cliente:** ___________________________
**El Proveedor:** ___________________________',
    'CIUDAD_FIRMA,FECHA_FIRMA,NOMBRE_CLIENTE,ID_CLIENTE,NOMBRE_PROVEEDOR,ID_PROVEEDOR,DESCRIPCION_SERVICIOS,PRECIO_TOTAL,MONEDA,PLAZO_PAGO,FECHA_FIN',
    true
)
ON CONFLICT (code) DO NOTHING;
