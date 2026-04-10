-- =====================================================================
-- V12: Módulo de Documentos con plantillas HTML (Thymeleaf)
-- =====================================================================

CREATE TABLE IF NOT EXISTS document_templates (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    jurisdiction VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    required_fields TEXT,
    field_definitions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

ALTER TABLE documents
ADD COLUMN IF NOT EXISTS case_request_id BIGINT REFERENCES case_requests(id) ON DELETE SET NULL,
ADD COLUMN IF NOT EXISTS content TEXT,
ADD COLUMN IF NOT EXISTS is_draft BOOLEAN DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_documents_case_request ON documents(case_request_id);

-- =====================================================================
-- SEED: Contrato de Arrendamiento de Bien Inmueble (Perú)
-- =====================================================================
INSERT INTO document_templates (public_id, name, code, jurisdiction, content, required_fields, field_definitions, is_active)
VALUES (
    gen_random_uuid(),
    'Contrato de Arrendamiento de Bien Inmueble',
    'RENT_PROPERTY',
    'Perú',
    '<div style="font-family: ''Times New Roman'', serif; font-size: 12pt; line-height: 1.8; color: #1e293b; max-width: 800px; margin: auto;">

<h2 style="text-align: center; font-size: 16pt; font-weight: bold; margin-bottom: 30px; text-transform: uppercase; letter-spacing: 1px;">
CONTRATO DE ARRENDAMIENTO DE BIEN INMUEBLE
</h2>

<p style="text-align: justify;">
Consta por el presente documento el <strong>CONTRATO DE ARRENDAMIENTO</strong> que celebran, de una parte, el
<span th:text="${NOMBRE_ARRENDADOR}" style="border-bottom: 1px solid #000; padding: 0 8px;">___________________</span>,
identificado con DNI Nº <span th:text="${DNI_ARRENDADOR}" style="border-bottom: 1px solid #000; padding: 0 8px;">_________</span>,
con domicilio <span th:text="${DOMICILIO_ARRENDADOR}" style="border-bottom: 1px solid #000; padding: 0 8px;">_____________________</span>,
distrito de <span th:text="${DISTRITO_ARRENDADOR}" style="border-bottom: 1px solid #000; padding: 0 8px;">__________</span>,
provincia y departamento de <span th:text="${DEPARTAMENTO_ARRENDADOR}" style="border-bottom: 1px solid #000; padding: 0 8px;">____</span>,
en adelante <strong>EL ARRENDADOR</strong>, y de otra parte
<span th:text="${NOMBRE_ARRENDATARIO}" style="border-bottom: 1px solid #000; padding: 0 8px;">___________</span>,
identificado con DNI Nº <span th:text="${DNI_ARRENDATARIO}" style="border-bottom: 1px solid #000; padding: 0 8px;">_________</span>,
con domicilio <span th:text="${DOMICILIO_ARRENDATARIO}" style="border-bottom: 1px solid #000; padding: 0 8px;">_____________________</span>,
distrito de <span th:text="${DISTRITO_ARRENDATARIO}" style="border-bottom: 1px solid #000; padding: 0 8px;">__________</span>,
provincia y departamento de <span th:text="${DEPARTAMENTO_ARRENDATARIO}" style="border-bottom: 1px solid #000; padding: 0 8px;">____</span>,
en adelante <strong>EL ARRENDATARIO</strong>, de acuerdo con los siguientes términos y condiciones:
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">PRIMERA: ANTECEDENTES</h3>
<p style="text-align: justify;">
EL ARRENDADOR es propietario del bien inmueble que se describe a continuación:
<span th:text="${DESCRIPCION_INMUEBLE}" style="border-bottom: 1px solid #000; padding: 0 8px;">_________________</span>,
que se encuentra inscrito en la Partida N°
<span th:text="${PARTIDA_REGISTRAL}" style="border-bottom: 1px solid #000; padding: 0 8px;">__________</span>
del Registro de Propiedad Inmueble de la Oficina Registral de
<span th:text="${OFICINA_REGISTRAL}" style="border-bottom: 1px solid #000; padding: 0 8px;">_______</span>,
en adelante el <strong>INMUEBLE</strong>.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">SEGUNDA: OBJETO DEL CONTRATO</h3>
<p style="text-align: justify;">
2.1 Por el presente contrato, EL ARRENDADOR otorga en arrendamiento el INMUEBLE a favor del ARRENDATARIO, el cual declara conocerlo plenamente.
</p>
<p style="text-align: justify;">
2.2 Por su parte, EL ARRENDATARIO se obliga a pagar la renta en la forma y oportunidad pactada en la cláusula Tercera del presente contrato.
</p>
<p style="text-align: justify;">
2.3 Las partes declaran que el INMUEBLE será destinado al uso de casa-habitación.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">TERCERA: DE LA RENTA</h3>
<p style="text-align: justify;">
3.1 La renta mensual pactada por el arrendamiento es de
<span th:text="${RENTA_MENSUAL}" style="border-bottom: 1px solid #000; padding: 0 8px;">____________</span>
(<span th:text="${RENTA_MENSUAL_LETRAS}" style="border-bottom: 1px solid #000; padding: 0 8px;">______________</span>),
pagaderos en forma adelantada el día 1 de cada mes.
</p>
<p style="text-align: justify;">
3.2 Con ocasión de la firma del presente documento, EL ARRENDATARIO entrega a EL ARRENDADOR la suma de
<span th:text="${ADELANTO_MONTO}" style="border-bottom: 1px solid #000; padding: 0 8px;">___________</span>
(<span th:text="${ADELANTO_MONTO_LETRAS}" style="border-bottom: 1px solid #000; padding: 0 8px;">_______________________</span>),
en calidad de <span th:text="${CONCEPTO_ADELANTO}" style="border-bottom: 1px solid #000; padding: 0 8px;">________________</span>.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">CUARTA: DURACIÓN DEL ARRENDAMIENTO</h3>
<p style="text-align: justify;">
El plazo de duración del presente contrato de arrendamiento es de
<span th:text="${PLAZO_ARRENDAMIENTO}" style="border-bottom: 1px solid #000; padding: 0 8px;">__________</span>,
contado a partir de la fecha de suscripción del presente documento. Sin embargo, dicho plazo podrá ser renovado por acuerdo entre las partes.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">QUINTA: SOBRE LA CONSERVACIÓN DEL INMUEBLE</h3>
<p style="text-align: justify;">
EL ARRENDATARIO está obligado a ejercer los actos inherentes a la conservación y cuidado del INMUEBLE arrendado, debiendo asumir los gastos de las reparaciones y refacciones originadas por el descuido, negligencia, maltrato y/o uso diario, así como por cualquier deterioro anormal.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">SEXTA: SOBRE LAS MEJORAS</h3>
<p style="text-align: justify;">
Las partes acuerdan que toda mejora efectuada por EL ARRENDATARIO, ya sea de carácter necesario, útil o de recreo, quedará en beneficio de EL ARRENDADOR, sin dar lugar a reembolso alguno de su parte.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">SÉPTIMA: SOBRE EL ESTADO DEL INMUEBLE</h3>
<p style="text-align: justify;">
7.1 EL ARRENDADOR declara que el INMUEBLE se entrega en perfecto estado, tanto en el terreno como en las construcciones e instalaciones y así declara recibirlo EL ARRENDATARIO.
</p>
<p style="text-align: justify;">
7.2 Asimismo, EL ARRENDADOR declara que el INMUEBLE materia de arrendamiento se encuentra libre de toda carga, gravamen o cualquier otro acto que pudiera limitar o impedir su uso por parte de EL ARRENDATARIO.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">OCTAVA: CESIÓN DE POSICIÓN CONTRACTUAL</h3>
<p style="text-align: justify;">
Las partes acuerdan que ninguna de ellas podrá ceder su posición en el presente contrato, salvo autorización expresa de la otra parte y siempre que la persona que entre en el contrato asuma de manera expresa las obligaciones correspondientes a su posición.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">NOVENA: PAGO DE SERVICIOS POR LA POSESIÓN DEL INMUEBLE</h3>
<p style="text-align: justify;">
Serán de cuenta de EL ARRENDATARIO los pagos mensuales correspondientes a los servicios vinculados con la posesión del inmueble dado en arrendamiento, tales como mantenimiento del edificio, electricidad, teléfono, internet, cable y arbitrios municipales. EL ARRENDADOR pagará el impuesto predial.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMA: SOBRE LA RESOLUCIÓN</h3>
<p style="text-align: justify;">
10.1 El presente contrato podrá ser resuelto de pleno derecho si se configura alguna de las siguientes causales:
</p>
<p style="text-align: justify; padding-left: 20px;">
a) Si EL ARRENDATARIO incumple con el pago de un mes de renta en la forma y oportunidad pactada según la cláusula Tercera del presente contrato.
</p>
<p style="text-align: justify; padding-left: 20px;">
b) Si por causa imputable a EL ARRENDADOR se vulnera o se impide el ejercicio del derecho de uso del INMUEBLE que tiene EL ARRENDATARIO en virtud del presente contrato.
</p>
<p style="text-align: justify;">
En cualquiera de los casos señalados anteriormente, la parte perjudicada con el incumplimiento podrá cursar una comunicación a la otra parte indicando su voluntad de resolver el contrato por haberse producido alguna de las causales indicadas. El contrato quedará resuelto a partir de la fecha de recepción de la comunicación antes señalada.
</p>
<p style="text-align: justify;">
10.2 El presente contrato podrá ser resuelto por cualquiera de las partes cuando se produzca el incumplimiento de cualquier obligación derivada del presente contrato. Para tal efecto, la parte afectada con el incumplimiento deberá requerir a la otra el cumplimiento de su obligación a fin que ésta realice su prestación en un plazo no menor de quince días. Si vencido dicho plazo la parte requerida no ha cumplido con su obligación, el contrato quedará resuelto a partir de dicha fecha.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMO PRIMERA: PROHIBICIÓN DE CEDER O SUBARRENDAR</h3>
<p style="text-align: justify;">
EL ARRENDATARIO se compromete a no traspasar o subarrendar total o parcialmente el inmueble dado en arrendamiento, sin la expresa autorización escrita de EL ARRENDADOR.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMO SEGUNDA: GARANTÍA</h3>
<p style="text-align: justify;">
Para garantizar el fiel cumplimiento de todas y cada una de las obligaciones asumidas por EL ARRENDATARIO conforme al presente contrato, EL ARRENDATARIO pagará a EL ARRENDADOR, a la firma del presente contrato, la suma de
<span th:text="${GARANTIA_MONTO}" style="border-bottom: 1px solid #000; padding: 0 8px;">____________</span>
(<span th:text="${GARANTIA_MONTO_LETRAS}" style="border-bottom: 1px solid #000; padding: 0 8px;">_______________</span>),
por concepto de depósito de garantía. Dicho importe no devengará intereses ni podrá ser aplicado al pago de alquileres devengados y será devuelto a EL ARRENDATARIO al finalizar el presente contrato en la misma moneda, previa comprobación del estado del inmueble y que los pagos de servicios y otros conceptos que correspondan a EL ARRENDATARIO hayan sido efectuados en su totalidad.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMO TERCERA: CLÁUSULA DE ALLANAMIENTO FUTURO</h3>
<p style="text-align: justify;">
De conformidad al Art. 5to. de la Ley No. 30201 que modifica el Art. 594 del Código Procesal Civil, EL ARRENDATARIO se allana desde ya a la demanda judicial para desocupar el inmueble dado en arrendamiento, por las causales de vencimiento de contrato de arrendamiento o por incumplimiento de pago de la renta de dos meses y quince días, de acuerdo a lo establecido en el Art. 330 y siguientes del Código Procesal Civil.
</p>
<p style="text-align: justify;">
En ese sentido, EL ARRENDATARIO se compromete a contradecir dicha demanda sólo si ha pagado las rentas convenidas y/o el contrato aún sigue vigente.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMO CUARTA: CLÁUSULA DE ALLANAMIENTO LEY 30933</h3>
<p style="text-align: justify;">
EL ARRENDATARIO declara que se allana a la solicitud de restitución del bien inmueble por vencimiento del plazo del contrato o la resolución del arrendamiento por falta de pago de la renta, conforme a lo establecido al Artículo 5 Inciso 1) de la Ley N° 30933, para efecto del procedimiento especial de desalojo a que se refiere la citada Ley.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMO QUINTA: REGISTRO ANTE CENTRALES DE RIESGO</h3>
<p style="text-align: justify;">
EL ARRENDATARIO otorga consentimiento a EL ARRENDADOR a fin de que pueda registrar la información y datos personales derivados del presente Contrato, incluyendo información sobre el cumplimiento e incumplimiento en el pago de la renta pactada, ante las Centrales Privadas de Información de Riesgos, conforme a ley.
</p>

<h3 style="margin-top: 30px; font-size: 13pt;">DÉCIMO SEXTA: LEGISLACIÓN APLICABLE Y JURISDICCIÓN</h3>
<p style="text-align: justify;">
En todo lo no regulado por el presente contrato, serán de aplicación las disposiciones legales de la República del Perú.
</p>
<p style="text-align: justify;">
En el improbable caso de litigio, ambas partes señalan como sus domicilios los que aparecen en la parte introductoria del presente contrato, sometiéndose expresamente a la jurisdicción de los jueces y tribunales de la provincia y departamento de Lima.
</p>

<p style="text-align: justify; margin-top: 30px;">
En señal de conformidad, las partes suscriben el presente documento a los
<span th:text="${DIA_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 8px;">___</span> días del mes de
<span th:text="${MES_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 8px;">__________</span> de
<span th:text="${ANIO_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 8px;">202_</span>.
</p>

<table style="width: 100%; margin-top: 80px; page-break-inside: avoid; border-collapse: collapse;">
  <tr>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; width: 200px; margin: 0 auto;">EL ARRENDADOR</div>
    </td>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; width: 200px; margin: 0 auto;">EL ARRENDATARIO</div>
    </td>
  </tr>
</table>

</div>',
    'NOMBRE_ARRENDADOR,DNI_ARRENDADOR,DOMICILIO_ARRENDADOR,DISTRITO_ARRENDADOR,DEPARTAMENTO_ARRENDADOR,NOMBRE_ARRENDATARIO,DNI_ARRENDATARIO,DOMICILIO_ARRENDATARIO,DISTRITO_ARRENDATARIO,DEPARTAMENTO_ARRENDATARIO,DESCRIPCION_INMUEBLE,PARTIDA_REGISTRAL,OFICINA_REGISTRAL,RENTA_MENSUAL,RENTA_MENSUAL_LETRAS,ADELANTO_MONTO,ADELANTO_MONTO_LETRAS,CONCEPTO_ADELANTO,PLAZO_ARRENDAMIENTO,GARANTIA_MONTO,GARANTIA_MONTO_LETRAS,DIA_FIRMA,MES_FIRMA,ANIO_FIRMA',
    '[
        {"name": "NOMBRE_ARRENDADOR", "label": "Nombre completo del Arrendador", "type": "TEXT", "required": true, "placeholder": "Ej: Juan Carlos Pérez López"},
        {"name": "DNI_ARRENDADOR", "label": "DNI del Arrendador", "type": "TEXT", "required": true, "placeholder": "Ej: 45678901"},
        {"name": "DOMICILIO_ARRENDADOR", "label": "Domicilio del Arrendador", "type": "TEXT", "required": true, "placeholder": "Ej: Av. Javier Prado 1234"},
        {"name": "DISTRITO_ARRENDADOR", "label": "Distrito del Arrendador", "type": "TEXT", "required": true, "placeholder": "Ej: San Isidro"},
        {"name": "DEPARTAMENTO_ARRENDADOR", "label": "Provincia y Departamento del Arrendador", "type": "TEXT", "required": true, "placeholder": "Ej: Lima"},
        {"name": "NOMBRE_ARRENDATARIO", "label": "Nombre completo del Arrendatario", "type": "TEXT", "required": true, "placeholder": "Ej: María López García"},
        {"name": "DNI_ARRENDATARIO", "label": "DNI del Arrendatario", "type": "TEXT", "required": true, "placeholder": "Ej: 12345678"},
        {"name": "DOMICILIO_ARRENDATARIO", "label": "Domicilio del Arrendatario", "type": "TEXT", "required": true, "placeholder": "Ej: Calle Los Olivos 567"},
        {"name": "DISTRITO_ARRENDATARIO", "label": "Distrito del Arrendatario", "type": "TEXT", "required": true, "placeholder": "Ej: Miraflores"},
        {"name": "DEPARTAMENTO_ARRENDATARIO", "label": "Provincia y Departamento del Arrendatario", "type": "TEXT", "required": true, "placeholder": "Ej: Lima"},
        {"name": "DESCRIPCION_INMUEBLE", "label": "Descripción del Inmueble", "type": "TEXT", "required": true, "placeholder": "Ej: Departamento 401, ubicado en Av. Arequipa 2345"},
        {"name": "PARTIDA_REGISTRAL", "label": "Partida Registral", "type": "TEXT", "required": true, "placeholder": "Ej: 12345678"},
        {"name": "OFICINA_REGISTRAL", "label": "Oficina Registral", "type": "TEXT", "required": true, "placeholder": "Ej: Lima"},
        {"name": "RENTA_MENSUAL", "label": "Renta mensual (cifra)", "type": "TEXT", "required": true, "placeholder": "Ej: S/ 2,500.00"},
        {"name": "RENTA_MENSUAL_LETRAS", "label": "Renta mensual (letras)", "type": "TEXT", "required": true, "placeholder": "Ej: Dos mil quinientos y 00/100 soles"},
        {"name": "ADELANTO_MONTO", "label": "Monto de adelanto", "type": "TEXT", "required": true, "placeholder": "Ej: S/ 2,500.00"},
        {"name": "ADELANTO_MONTO_LETRAS", "label": "Monto de adelanto (letras)", "type": "TEXT", "required": true, "placeholder": "Ej: Dos mil quinientos soles"},
        {"name": "CONCEPTO_ADELANTO", "label": "Concepto del adelanto", "type": "TEXT", "required": true, "placeholder": "Ej: primer mes de arrendamiento"},
        {"name": "PLAZO_ARRENDAMIENTO", "label": "Plazo del arrendamiento", "type": "TEXT", "required": true, "placeholder": "Ej: 12 meses"},
        {"name": "GARANTIA_MONTO", "label": "Monto de garantía", "type": "TEXT", "required": true, "placeholder": "Ej: S/ 2,500.00"},
        {"name": "GARANTIA_MONTO_LETRAS", "label": "Monto de garantía (letras)", "type": "TEXT", "required": true, "placeholder": "Ej: Dos mil quinientos soles"},
        {"name": "DIA_FIRMA", "label": "Día de firma", "type": "TEXT", "required": true, "placeholder": "Ej: 15"},
        {"name": "MES_FIRMA", "label": "Mes de firma", "type": "TEXT", "required": true, "placeholder": "Ej: abril"},
        {"name": "ANIO_FIRMA", "label": "Año de firma", "type": "TEXT", "required": true, "placeholder": "Ej: 2026"}
    ]',
    true
) ON CONFLICT (code) DO NOTHING;
