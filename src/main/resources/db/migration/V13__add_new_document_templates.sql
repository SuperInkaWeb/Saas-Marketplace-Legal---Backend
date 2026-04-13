-- =====================================================================
-- V13: Agregar Plantillas de Documentos (Laboral, NDA, EIRL)
-- =====================================================================

INSERT INTO document_templates (public_id, name, code, jurisdiction, content, required_fields, field_definitions, is_active)
VALUES 
-- =====================================================================
-- 1. Contrato de Trabajo Sujeto a Modalidad
-- =====================================================================
(
    gen_random_uuid(),
    'Contrato de Trabajo Sujeto a Modalidad',
    'LABOR_MODALIDAD_01',
    'Perú',
    '<div style="font-family: ''Times New Roman'', serif; font-size: 11pt; line-height: 1.6; color: #1e293b; max-width: 800px; margin: auto;">

<h2 style="text-align: center; font-size: 14pt; font-weight: bold; margin-bottom: 30px; text-transform: uppercase;">
MODELO DE CONTRATO DE TRABAJO SUJETO A MODALIDAD
</h2>

<p style="text-align: justify;">
Conste por el presente documento, que se suscribe por triplicado con igual tenor y valor, el contrato de trabajo sujeto a modalidad que al amparo del Texto Único Ordenado del Decreto Legislativo Nº 728, Decreto Supremo Nº 003-97-TR, Ley de Productividad y Competitividad Laboral y normas complementarias, que celebran de una parte <span th:text="${NOMBRE_EMPRESA}" style="border-bottom: 1px solid #000; padding: 0 4px; font-weight: bold;">___________________________</span>, con R.U.C. Nº <span th:text="${RUC_EMPRESA}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________</span> y domicilio real en <span th:text="${DOMICILIO_EMPRESA}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________________________</span>, debidamente representada por su <span th:text="${CARGO_REPRESENTANTE}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________</span> señor(a) <span th:text="${NOMBRE_REPRESENTANTE}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span>, con D.N.I. Nº <span th:text="${DNI_REPRESENTANTE}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________</span>, a quien en adelante se le denominará <strong>EL EMPLEADOR</strong>, y de la otra parte, don(ña) <span th:text="${NOMBRE_TRABAJADOR}" style="border-bottom: 1px solid #000; padding: 0 4px; font-weight: bold;">___________________________</span>, con D.N.I. Nº <span th:text="${DNI_TRABAJADOR}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________</span>, domiciliado en <span th:text="${DOMICILIO_TRABAJADOR}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________________________</span>, a quien en adelante se le denominará <strong>EL TRABAJADOR</strong>, en los términos y condiciones siguientes:
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>1.-</strong> EL EMPLEADOR es una <span th:text="${TIPO_EMPRESA}" style="border-bottom: 1px solid #000; padding: 0 4px;">_____________________</span>, cuyo objeto social es <span th:text="${OBJETO_SOCIAL_EMPRESA}" style="border-bottom: 1px solid #000; padding: 0 4px;">____________________________________________________</span> y que ha sido debidamente autorizada por <span th:text="${AUTORIZADA_POR}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span>, de fecha <span th:text="${FECHA_AUTORIZACION}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span>, emitida por <span th:text="${EMITIDA_POR}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span>, que requiere de los servicios del TRABAJADOR en forma <span th:text="${TIPO_MODALIDAD}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span>, para <span th:text="${CAUSAS_MODALIDAD}" style="border-bottom: 1px solid #000; padding: 0 4px;">_____________________________________________________________</span>.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>2.-</strong> Por el presente contrato, EL TRABAJADOR se obliga a prestar sus servicios al EMPLEADOR para realizar las siguientes actividades: <span th:text="${ACTIVIDADES}" style="border-bottom: 1px solid #000; padding: 0 4px;">____________________________________________________________________</span>, debiendo someterse al cumplimiento estricto de la labor, para la cual ha sido contratado, bajo las directivas de sus jefes o instructores, y las que se impartan por necesidades del servicio en ejercicio de las facultades de administración y dirección de la empresa, de conformidad con el artículo 9º del Texto Único Ordenado de la Ley de Productividad y Competitividad Laboral, aprobado por Decreto Supremo Nº 003-97-TR.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>3.-</strong> La duración del presente contrato es de <span th:text="${PLAZO_CONTRATO}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________</span>, iniciándose el día <span th:text="${DIA_INICIO}" style="border-bottom: 1px solid #000; padding: 0 4px;">______</span> de <span th:text="${MES_INICIO}" style="border-bottom: 1px solid #000; padding: 0 4px;">________________</span> de <span th:text="${ANIO_INICIO}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______</span> y concluirá el día <span th:text="${DIA_FIN}" style="border-bottom: 1px solid #000; padding: 0 4px;">______</span> de <span th:text="${MES_FIN}" style="border-bottom: 1px solid #000; padding: 0 4px;">________________</span> de <span th:text="${ANIO_FIN}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______</span>.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>4.-</strong> En contraprestación a los servicios del TRABAJADOR, el EMPLEADOR se obliga a pagar una remuneración <span th:text="${FRECUENCIA_PAGO}" style="border-bottom: 1px solid #000; padding: 0 4px;">_________________</span> de S/ <span th:text="${R_MONTO}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> (<span th:text="${R_MONTO_LETRAS}" style="border-bottom: 1px solid #000; padding: 0 4px;">________________________________________</span>). Igualmente se obliga a facilitar al trabajador los materiales necesarios para que desarrolle sus actividades, y a otorgarle los beneficios que por ley, pacto o costumbre tuvieran los trabajadores del centro de trabajo contratados a plazo indeterminado.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>5.-</strong> EL TRABAJADOR deberá prestar sus servicios en el siguiente horario: de <span th:text="${HORARIO_DIAS}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span>, de <span th:text="${HORA_INICIO}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> a <span th:text="${HORA_FIN}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> horas, teniendo un refrigerio de <span th:text="${MINUTOS_REFRI}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> minutos, que será tomado de <span th:text="${HORA_INI_REFRI}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> a <span th:text="${HORA_FIN_REFRI}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>6.-</strong> EL EMPLEADOR, se obliga a inscribir al TRABAJADOR en el libro de Planillas de Remuneraciones, así como poner a conocimiento de la Autoridad Administrativa de Trabajo el presente contrato, para su conocimiento y registro, en cumplimiento de los dispuesto por artículo 73° del Texto Único ordenado del Decreto Legislativo N° 728, Ley de Productividad y Competitividad laboral, aprobado mediante Decreto Supremo N° 003-97-TR.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>7.-</strong> Queda entendido que EL EMPLEADOR no está obligado a dar aviso alguno adicional referente al término del presente contrato, operando su extinción en la fecha de su vencimiento, conforme a la cláusula tercera, oportunidad en la cual se abonará al TRABAJADOR los beneficios sociales, que le pudieran corresponder de acuerdo a Ley.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>8.-</strong> En todo lo no previsto por el presente contrato, se estará a las disposiciones laborales que regulan los contratos de trabajo sujeto a modalidad, contenidos en el Texto Único Ordenado del Decreto Legislativo N° 728 aprobado por el Decreto Supremo N° 003-97-TR, Ley de Productividad y Competitividad Laboral.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>9.-</strong> Las partes contratantes renuncian expresamente al fuero judicial de sus domicilios y se someten a la jurisdicción de los jueces de <span th:text="${JURISDICCION_CIUDAD}" style="border-bottom: 1px solid #000; padding: 0 4px;">___________________________</span> para resolver cualquier controversia que el cumplimiento del presente contrato pudiera originar.
</p>

<p style="text-align: justify; margin-top: 30px;">
Firmado en <span th:text="${JURISDICCION_CIUDAD}" style="border-bottom: 1px solid #000; padding: 0 4px;">_________________</span> a los <span th:text="${DIA_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">___</span> días del mes de <span th:text="${MES_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> de <span th:text="${ANIO_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______</span>.
</p>

<table style="width: 100%; margin-top: 80px; page-break-inside: avoid; border-collapse: collapse;">
  <tr>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; width: 220px; margin: 0 auto;">EMPLEADOR</div>
    </td>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; width: 220px; margin: 0 auto;">TRABAJADOR</div>
    </td>
  </tr>
</table>

</div>',
    '',
    '[
      {"name": "NOMBRE_EMPRESA", "label": "Nombre o Razón Social del Empleador", "type": "TEXT", "required": true},
      {"name": "RUC_EMPRESA", "label": "RUC del Empleador", "type": "TEXT", "required": true},
      {"name": "DOMICILIO_EMPRESA", "label": "Domicilio del Empleador", "type": "TEXT", "required": true},
      {"name": "CARGO_REPRESENTANTE", "label": "Cargo del Representante", "type": "TEXT", "required": true, "placeholder": "Ej: Gerente General"},
      {"name": "NOMBRE_REPRESENTANTE", "label": "Nombre del Representante", "type": "TEXT", "required": true},
      {"name": "DNI_REPRESENTANTE", "label": "DNI del Representante", "type": "TEXT", "required": true},
      {"name": "NOMBRE_TRABAJADOR", "label": "Nombre del Trabajador", "type": "TEXT", "required": true},
      {"name": "DNI_TRABAJADOR", "label": "DNI del Trabajador", "type": "TEXT", "required": true},
      {"name": "DOMICILIO_TRABAJADOR", "label": "Domicilio del Trabajador", "type": "TEXT", "required": true},
      {"name": "TIPO_EMPRESA", "label": "Tipo de Empresa", "type": "TEXT", "required": true, "placeholder": "Ej: Sociedad Anónima Cerrada"},
      {"name": "OBJETO_SOCIAL_EMPRESA", "label": "Objeto Social de la Empresa", "type": "TEXT", "required": true},
      {"name": "AUTORIZADA_POR", "label": "Autorizada por", "type": "TEXT", "required": false},
      {"name": "FECHA_AUTORIZACION", "label": "Fecha de Autorización", "type": "TEXT", "required": false},
      {"name": "EMITIDA_POR", "label": "Emitida por", "type": "TEXT", "required": false},
      {"name": "TIPO_MODALIDAD", "label": "Modalidad de Contratación", "type": "SELECT", "required": true, "options": ["En forma temporal", "En forma accidental", "Para obra determinada", "Para servicio específico"]},
      {"name": "CAUSAS_MODALIDAD", "label": "Causas de la modalidad", "type": "TEXT", "required": true},
      {"name": "ACTIVIDADES", "label": "Actividades a realizar", "type": "TEXT", "required": true},
      {"name": "PLAZO_CONTRATO", "label": "Plazo Total", "type": "TEXT", "required": true, "placeholder": "Ej: 6 meses"},
      {"name": "DIA_INICIO", "label": "Día de inicio", "type": "TEXT", "required": true},
      {"name": "MES_INICIO", "label": "Mes de inicio", "type": "TEXT", "required": true},
      {"name": "ANIO_INICIO", "label": "Año de inicio", "type": "TEXT", "required": true},
      {"name": "DIA_FIN", "label": "Día de fin", "type": "TEXT", "required": true},
      {"name": "MES_FIN", "label": "Mes de fin", "type": "TEXT", "required": true},
      {"name": "ANIO_FIN", "label": "Año de fin", "type": "TEXT", "required": true},
      {"name": "FRECUENCIA_PAGO", "label": "Frecuencia de pago", "type": "SELECT", "required": true, "options": ["mensual", "quincenal", "semanal"]},
      {"name": "R_MONTO", "label": "Remuneración (Números)", "type": "TEXT", "required": true},
      {"name": "R_MONTO_LETRAS", "label": "Remuneración (Letras)", "type": "TEXT", "required": true},
      {"name": "HORARIO_DIAS", "label": "Días de trabajo", "type": "TEXT", "required": true, "placeholder": "Ej: Lunes a Viernes"},
      {"name": "HORA_INICIO", "label": "Hora de inicio", "type": "TEXT", "required": true},
      {"name": "HORA_FIN", "label": "Hora de fin", "type": "TEXT", "required": true},
      {"name": "MINUTOS_REFRI", "label": "Minutos de refrigerio", "type": "TEXT", "required": true},
      {"name": "HORA_INI_REFRI", "label": "Hora inicio refrigerio", "type": "TEXT", "required": false},
      {"name": "HORA_FIN_REFRI", "label": "Hora fin refrigerio", "type": "TEXT", "required": false},
      {"name": "JURISDICCION_CIUDAD", "label": "Ciudad (Jurisdicción)", "type": "TEXT", "required": true},
      {"name": "DIA_FIRMA", "label": "Día de firma", "type": "TEXT", "required": true},
      {"name": "MES_FIRMA", "label": "Mes de firma", "type": "TEXT", "required": true},
      {"name": "ANIO_FIRMA", "label": "Año de firma", "type": "TEXT", "required": true}
    ]',
    true
),
-- =====================================================================
-- 2. Acuerdo de Confidencialidad Bilateral (NDA)
-- =====================================================================
(
    gen_random_uuid(),
    'Acuerdo de Confidencialidad Bilateral (NDA)',
    'NDA_BILATERAL_01',
    'Perú',
    '<div style="font-family: ''Times New Roman'', serif; font-size: 11pt; line-height: 1.6; color: #1e293b; max-width: 800px; margin: auto;">

<h2 style="text-align: center; font-size: 14pt; font-weight: bold; margin-bottom: 20px; text-transform: uppercase;">
ACUERDO DE CONFIDENCIALIDAD BILATERAL
</h2>

<p style="text-align: justify;">
Conste por el presente documento, el Acuerdo de Confidencialidad Bilateral (en adelante, el "Acuerdo"), que celebran de mutuo acuerdo y sujeto a las leyes de la República del Perú:
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>De una parte:</strong><br>
<span th:text="${NOMBRE_PARTE_A}" style="border-bottom: 1px solid #000; padding: 0 4px; font-weight: bold;">_______________________</span>, identificada con <span th:text="${TIPO_DOC_A}">DNI/RUC</span> N° <span th:text="${NUM_DOC_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>, con domicilio legal en <span th:text="${DOMICILIO_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________________________</span>, debidamente representada por su <span th:text="${CARGO_REP_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________</span>, el señor/a <span th:text="${NOMBRE_REP_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________________</span>, identificado con DNI N° <span th:text="${DNI_REP_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>, según poder inscrito en la Partida Electrónica N° <span th:text="${PARTIDA_REG_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> del Registro de Personas Jurídicas de la SUNARP de la Zona Registral N° <span th:text="${ZONA_REG_A}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>; a quien en adelante se le denominará <strong>"LA PARTE REVELADORA / RECEPTORA A"</strong>;
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>Y de la otra parte:</strong><br>
<span th:text="${NOMBRE_PARTE_B}" style="border-bottom: 1px solid #000; padding: 0 4px; font-weight: bold;">_______________________</span>, identificada con <span th:text="${TIPO_DOC_B}">DNI/RUC</span> N° <span th:text="${NUM_DOC_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>, con domicilio legal en <span th:text="${DOMICILIO_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________________________</span>, debidamente representada por su <span th:text="${CARGO_REP_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________</span>, el señor/a <span th:text="${NOMBRE_REP_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______________________</span>, identificado con DNI N° <span th:text="${DNI_REP_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>, según poder inscrito en la Partida Electrónica N° <span th:text="${PARTIDA_REG_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> del Registro de Personas Jurídicas de la SUNARP de la Zona Registral N° <span th:text="${ZONA_REG_B}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span>; a quien en adelante se le denominará <strong>"LA PARTE REVELADORA / RECEPTORA B"</strong>.
</p>

<p style="text-align: justify; margin-top: 15px;">
Ambas partes de manera conjunta serán denominadas como "LAS PARTES". Este Acuerdo se celebra bajo los siguientes términos y condiciones:
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA PRIMERA: ANTECEDENTES Y OBJETO DEL ACUERDO</h3>
<p style="text-align: justify;">
<strong>1.1.</strong> LAS PARTES se encuentran en un proceso de negociación y/o desarrollo de un proyecto de colaboración comercial, técnica o estratégica consistente en: <span th:text="${PROPOSITO_ACUERDO}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________________________________________________________________________</span> (en adelante, el "Propósito").<br>
<strong>1.2.</strong> Para la evaluación y ejecución del Propósito, LAS PARTES acuerdan que será estrictamente necesario compartirse mutuamente información de carácter sensible, estratégica y privada.<br>
<strong>1.3.</strong> El objeto del presente Acuerdo es establecer los términos y condiciones bajo los cuales LAS PARTES se comprometen a proteger, mantener en estricta reserva y no divulgar a terceros la Información Confidencial compartida entre sí.
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA SEGUNDA: DEFINICIÓN DE INFORMACIÓN CONFIDENCIAL</h3>
<p style="text-align: justify;">
Para efectos de este Acuerdo, se entenderá por "Información Confidencial" toda aquella información protegida por el secreto bancario, tributario, comercial, industrial, tecnológico y bursátil; así como cualquier dato, diseño, modelo de negocio, bases de datos, código fuente, estado financiero o conocimiento <i>know-how</i> que sea transmitida de forma oral, escrita, electrónica o por cualquier otro medio por una de las partes a la otra. Toda información compartida en el marco del Propósito se presumirá confidencial.
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA TERCERA: EXCLUSIONES DE CONFIDENCIALIDAD</h3>
<p style="text-align: justify;">
No se considerará Información Confidencial aquella información que: a) Sea de conocimiento público; b) Ya estuviera en posesión legítima previa; c) Sea recibida legítimamente de un tercero; d) Cuente con autorización expresa; e) Sea requerida por mandato de una autoridad peruana.
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA CUARTA: OBLIGACIONES DE LAS PARTES</h3>
<p style="text-align: justify;">
Toda Parte que reciba Información Confidencial se obliga estrictamente a utilizarla única y exclusivamente para el desarrollo del Propósito, mantenerla en reserva, y restringir el acceso únicamente a quienes tengan "necesidad de saber".
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA QUINTA: PROPIEDAD DE LA INFORMACIÓN</h3>
<p style="text-align: justify;">
Toda Información Confidencial proporcionada es y seguirá siendo propiedad exclusiva de la Parte Reveladora. La entrega no otorga derechos de propiedad intelectual.
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA SEXTA: DURACIÓN DEL ACUERDO</h3>
<p style="text-align: justify;">
El presente Acuerdo entrará en vigencia a partir de la fecha de su suscripción y mantendrá su vigencia por un plazo de <span th:text="${PLAZO_VIGENCIA}" style="border-bottom: 1px solid #000; padding: 0 4px;">______________</span>. La obligación de reserva sobre el secreto comercial o industrial sobrevivirá de manera indefinida.
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA SÉPTIMA: PENALIDADES POR INCUMPLIMIENTO</h3>
<p style="text-align: justify;">
En caso de que cualquiera de LAS PARTES incumpla, la parte infractora deberá pagar a la parte afectada, en calidad de penalidad, la suma de S/ <span th:text="${PENALIDAD_MONTO}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> (<span th:text="${PENALIDAD_LETRAS}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________________________________</span>). El pago es independiente de la indemnización por los daños reales y las acciones penales que correspondan (Art. 165 del Código Penal Peruano).
</p>

<h3 style="margin-top: 25px; font-size: 11pt;">CLÁUSULA OCTAVA: LEGISLACIÓN Y JURISDICCIÓN</h3>
<p style="text-align: justify;">
Para cualquier controversia, LAS PARTES renuncian al fuero de sus domicilios y se someten a la jurisdicción de los Jueces y Tribunales del Distrito Judicial de <span th:text="${JURISDICCION_CIUDAD}" style="border-bottom: 1px solid #000; padding: 0 4px;">_____________________</span>.
</p>

<p style="text-align: justify; margin-top: 30px;">
Estando de acuerdo, LAS PARTES lo suscriben en la ciudad de <span th:text="${CIUDAD_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">____________</span>, a los <span th:text="${DIA_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">___</span> días del mes de <span th:text="${MES_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">__________</span> del año <span th:text="${ANIO_FIRMA}" style="border-bottom: 1px solid #000; padding: 0 4px;">_______</span>.
</p>

<table style="width: 100%; margin-top: 80px; page-break-inside: avoid; border-collapse: collapse;">
  <tr>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; margin: 0 auto;">P.p. <span th:text="${NOMBRE_PARTE_A}">_____________________</span></div>
      <div style="font-size: 10pt;">Representante: <span th:text="${NOMBRE_REP_A}">_____________________</span></div>
      <div style="font-size: 10pt;">DNI: <span th:text="${DNI_REP_A}">_____________________</span></div>
    </td>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; margin: 0 auto;">P.p. <span th:text="${NOMBRE_PARTE_B}">_____________________</span></div>
      <div style="font-size: 10pt;">Representante: <span th:text="${NOMBRE_REP_B}">_____________________</span></div>
      <div style="font-size: 10pt;">DNI: <span th:text="${DNI_REP_B}">_____________________</span></div>
    </td>
  </tr>
</table>

</div>',
    '',
    '[
      {"name": "NOMBRE_PARTE_A", "label": "Nombre o Razón Social (Parte A)", "type": "TEXT", "required": true},
      {"name": "TIPO_DOC_A", "label": "Tipo Doc. (Parte A)", "type": "SELECT", "required": true, "options": ["RUC", "DNI", "CE"]},
      {"name": "NUM_DOC_A", "label": "Número Documento (Parte A)", "type": "TEXT", "required": true},
      {"name": "DOMICILIO_A", "label": "Domicilio (Parte A)", "type": "TEXT", "required": true},
      {"name": "CARGO_REP_A", "label": "Cargo Representante (Parte A)", "type": "TEXT", "required": true, "placeholder": "Ej: Gerente General"},
      {"name": "NOMBRE_REP_A", "label": "Nombre Representante (Parte A)", "type": "TEXT", "required": true},
      {"name": "DNI_REP_A", "label": "DNI Representante (Parte A)", "type": "TEXT", "required": true},
      {"name": "PARTIDA_REG_A", "label": "Partida Registral (Parte A)", "type": "TEXT", "required": false},
      {"name": "ZONA_REG_A", "label": "Zona Registral (Parte A)", "type": "TEXT", "required": false},
      {"name": "NOMBRE_PARTE_B", "label": "Nombre o Razón Social (Parte B)", "type": "TEXT", "required": true},
      {"name": "TIPO_DOC_B", "label": "Tipo Doc. (Parte B)", "type": "SELECT", "required": true, "options": ["RUC", "DNI", "CE"]},
      {"name": "NUM_DOC_B", "label": "Número Documento (Parte B)", "type": "TEXT", "required": true},
      {"name": "DOMICILIO_B", "label": "Domicilio (Parte B)", "type": "TEXT", "required": true},
      {"name": "CARGO_REP_B", "label": "Cargo Representante (Parte B)", "type": "TEXT", "required": true},
      {"name": "NOMBRE_REP_B", "label": "Nombre Representante (Parte B)", "type": "TEXT", "required": true},
      {"name": "DNI_REP_B", "label": "DNI Representante (Parte B)", "type": "TEXT", "required": true},
      {"name": "PARTIDA_REG_B", "label": "Partida Registral (Parte B)", "type": "TEXT", "required": false},
      {"name": "ZONA_REG_B", "label": "Zona Registral (Parte B)", "type": "TEXT", "required": false},
      {"name": "PROPOSITO_ACUERDO", "label": "Propósito del Acuerdo", "type": "TEXT", "required": true},
      {"name": "PLAZO_VIGENCIA", "label": "Plazo de vigencia", "type": "TEXT", "required": true, "placeholder": "Ej: 5 años"},
      {"name": "PENALIDAD_MONTO", "label": "Monto Penalidad (Números)", "type": "TEXT", "required": true},
      {"name": "PENALIDAD_LETRAS", "label": "Monto Penalidad (Letras)", "type": "TEXT", "required": true},
      {"name": "JURISDICCION_CIUDAD", "label": "Jurisdicción Judicial", "type": "TEXT", "required": true, "placeholder": "Ej: Lima Cercado"},
      {"name": "CIUDAD_FIRMA", "label": "Ciudad de Firma", "type": "TEXT", "required": true},
      {"name": "DIA_FIRMA", "label": "Día de firma", "type": "TEXT", "required": true},
      {"name": "MES_FIRMA", "label": "Mes de firma", "type": "TEXT", "required": true},
      {"name": "ANIO_FIRMA", "label": "Año de firma", "type": "TEXT", "required": true}
    ]',
    true
),
-- =====================================================================
-- 3. Minuta de Constitución E.I.R.L.
-- =====================================================================
(
    gen_random_uuid(),
    'Minuta de Constitución de E.I.R.L.',
    'MINUTA_EIRL_01',
    'Perú',
    '<div style="font-family: ''Times New Roman'', serif; font-size: 11pt; line-height: 1.6; color: #1e293b; max-width: 800px; margin: auto;">

<h2 style="text-align: center; font-size: 14pt; font-weight: bold; margin-bottom: 30px; text-transform: uppercase;">
MINUTA DE CONSTITUCIÓN DE EMPRESA INDIVIDUAL DE RESPONSABILIDAD LIMITADA (E.I.R.L.)
</h2>

<p style="text-align: justify;">
<strong>SEÑOR NOTARIO:</strong><br><br>
SÍRVASE USTED EXTENDER EN SU REGISTRO DE ESCRITURAS PÚBLICAS UNA DE CONSTITUCIÓN DE EMPRESA INDIVIDUAL DE RESPONSABILIDAD LIMITADA, QUE OTORGA: <span th:text="${NOMBRE_TITULAR}" style="font-weight: bold;">_______________________</span>, DE NACIONALIDAD <span th:text="${NACIONALIDAD_TITULAR}">______________</span>, CON DOCUMENTO DE IDENTIDAD NÚMERO <span th:text="${NUM_DOC_TITULAR}">__________</span>, OCUPACIÓN: <span th:text="${OCUPACION_TITULAR}">______________</span>, ESTADO CIVIL: <span th:text="${ESTADO_CIVIL_TITULAR}">______________</span>.<br><br>
INTERVIENE TAMBIÉN SU CÓNYUGE, <span th:text="${NOMBRE_CONYUGE}">_______________________</span>, CON DOCUMENTO DE IDENTIDAD NÚMERO <span th:text="${NUM_DOC_CONYUGE}">__________</span>, A FIN DE PRESTAR SU EXPRESO CONSENTIMIENTO PARA EL APORTE.<br><br>
SEÑALANDO DOMICILIO PARA EFECTOS DE ESTE INSTRUMENTO EN <span th:text="${DOMICILIO_TITULAR}">__________________________________</span>, EN LOS TÉRMINOS Y CONDICIONES SIGUIENTES:
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>PRIMERA.- DENOMINACIÓN Y DOMICILIO</strong><br>
POR EL PRESENTE INSTRUMENTO, EL TITULAR: CONSTITUYE UNA EMPRESA INDIVIDUAL DE RESPONSABILIDAD LIMITADA BAJO LA DENOMINACIÓN DE: "<strong><span th:text="${NOMBRE_EMPRESA}">_______________________</span> E.I.R.L.</strong>", CON DOMICILIO EN <span th:text="${DISTRITO_EMPRESA}">__________</span>, PROVINCIA DE <span th:text="${PROVINCIA_EMPRESA}">__________</span>, DEPARTAMENTO DE <span th:text="${DEPARTAMENTO_EMPRESA}">__________</span>. LA EMPRESA INICIA SUS OPERACIONES A PARTIR DE SU INSCRIPCIÓN EN EL REGISTRO DE PERSONAS JURÍDICAS CON UNA DURACIÓN INDETERMINADA, PUDIENDO ESTABLECER SUCURSALES EN TODO EL TERRITORIO NACIONAL.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>SEGUNDA.- OBJETO DE LA EMPRESA</strong><br>
EL OBJETO DE LA EMPRESA ES: <span th:text="${OBJETO_SOCIAL}">____________________________________________________________________</span>. SE ENTIENDEN INCLUIDOS EN EL OBJETO, LOS ACTOS RELACIONADOS CON EL MISMO, QUE COADYUVEN A LA REALIZACIÓN DE SUS FINES EMPRESARIALES. PARA CUMPLIR DICHO OBJETO, PODRÁ REALIZAR TODOS AQUELLOS ACTOS Y CONTRATOS QUE SEAN LÍCITOS, SIN RESTRICCIÓN ALGUNA.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>TERCERA.- EL CAPITAL DE LA EMPRESA</strong><br>
EL CAPITAL DE LA EMPRESA ES DE S/ <span th:text="${CAPITAL_NUMEROS}">__________</span> (<span th:text="${CAPITAL_LETRAS}">____________________________</span>), CONSTITUIDO POR EL APORTE DEL TITULAR DE LA SIGUIENTE MANERA: <br><br>
&gt;&gt; <span th:text="${TIPO_APORTE}" style="font-weight:bold; color: #1d4ed8;">_______________________</span>
<br><em>(Nota: El abogado deberá ajustar la redacción del aporte en el Editor Legal de acuerdo a la declaración jurada o depósito bancario).</em>
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>CUARTA.- ÓRGANOS DE LA EMPRESA</strong><br>
SON ÓRGANOS DE LA EMPRESA: EL TITULAR Y LA GERENCIA. EL RÉGIMEN QUE LE CORRESPONDA ESTÁ SEÑALADO EN EL DECRETO LEY Nº 21621, ARTÍCULOS 39 Y 50 RESPECTIVAMENTE, Y DEMÁS NORMAS MODIFICATORIAS Y COMPLEMENTARIAS.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>QUINTA.- DE LA GERENCIA</strong><br>
LA GERENCIA ES EL ÓRGANO QUE TIENE A SU CARGO LA ADMINISTRACIÓN Y REPRESENTACIÓN DE LA EMPRESA. SERÁ DESEMPEÑADA POR UNA O MÁS PERSONAS NATURALES. EL CARGO DE GERENTE ES INDELEGABLE. EN CASO DE QUE EL CARGO DE GERENTE RECAIGA EN EL TITULAR, ÉSTE SE DENOMINARÁ TITULAR GERENTE.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>SEXTA.- DESIGNACIÓN DEL GERENTE</strong><br>
LA DESIGNACIÓN DEL GERENTE SERÁ EFECTUADA POR EL TITULAR; LA DURACIÓN DEL CARGO ES INDEFINIDA, AUNQUE PUEDE SER REVOCADO EN CUALQUIER MOMENTO.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>SÉPTIMA.- FACULTADES DEL GERENTE</strong><br>
CORRESPONDE AL GERENTE ORGANIZAR EL RÉGIMEN INTERNO; CELEBRAR CONTRATOS INHERENTES AL OBJETO; REPRESENTAR A LA EMPRESA ANTE TODA CLASE DE AUTORIDADES; ABRIR Y CERRAR CUENTAS CORRIENTES BANCARIAS; SOLICITAR PRÉSTAMOS, SUSCRIBIR CONTRATOS Y SOLICITAR REGISTROS DE PATENTES Y MARCAS EN INDECOPI.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>OCTAVA.- NORMAS SUPLETORIAS</strong><br>
PARA TODO LO NO PREVISTO RIGEN LAS DISPOSICIONES CONTENIDAS EN EL D.L. Nº 21621 Y AQUELLAS QUE LAS MODIFIQUEN O COMPLEMENTEN.
</p>

<p style="text-align: justify; margin-top: 15px;">
<strong>DISPOSICIÓN TRANSITORIA.- NOMBRAMIENTO DE CARGOS</strong><br>
<span th:text="${NOMBRE_GERENTE}">_______________________</span>, IDENTIFICADO CON DNI/CE Nº <span th:text="${NUM_DOC_GERENTE}">__________</span>, EJERCERÁ EL CARGO DE TITULAR GERENTE DE LA EMPRESA.
</p>

<p style="text-align: justify; margin-top: 25px;">
AGREGUE USTED, SEÑOR NOTARIO, LA INTRODUCCIÓN Y CONCLUSIÓN DE LEY, Y SÍRVASE CURSAR LOS PARTES RESPECTIVOS AL REGISTRO DE PERSONAS JURÍDICAS DE LA ZONA REGISTRAL.<br><br>
<span th:text="${CIUDAD_FIRMA}">_________</span>, <span th:text="${DIA_FIRMA}">___</span> DE <span th:text="${MES_FIRMA}">__________</span> DE 20<span th:text="${ANIO_FIRMA}">__</span>.
</p>

<table style="width: 100%; margin-top: 60px; page-break-inside: avoid; border-collapse: collapse;">
  <tr>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; margin: 0 auto; width: 200px;">TITULAR</div>
      <div style="font-size: 10pt;">DNI: <span th:text="${NUM_DOC_TITULAR}">_____________________</span></div>
    </td>
    <td style="width: 50%; text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; margin: 0 auto; width: 200px;">CÓNYUGE</div>
      <div style="font-size: 10pt;">DNI: <span th:text="${NUM_DOC_CONYUGE}">_____________________</span></div>
    </td>
  </tr>
</table>

<table style="width: 100%; margin-top: 60px; page-break-inside: avoid; border-collapse: collapse;">
  <tr>
    <td style="text-align: center; padding: 0 20px;">
      <div style="border-top: 1px solid #000; padding-top: 8px; font-weight: bold; margin: 0 auto; width: 260px;">ABOGADO</div>
      <div style="font-size: 10pt;">C.A.L. N°: <span th:text="${CAL_ABOGADO}">_____________________</span></div>
    </td>
  </tr>
</table>

</div>',
    '',
    '[
      {"name": "NOMBRE_TITULAR", "label": "Nombre del Titular", "type": "TEXT", "required": true},
      {"name": "NACIONALIDAD_TITULAR", "label": "Nacionalidad del Titular", "type": "TEXT", "required": true, "placeholder": "Ej: Peruano"},
      {"name": "NUM_DOC_TITULAR", "label": "Número Documento (Titular)", "type": "TEXT", "required": true},
      {"name": "OCUPACION_TITULAR", "label": "Ocupación (Titular)", "type": "TEXT", "required": true},
      {"name": "ESTADO_CIVIL_TITULAR", "label": "Estado Civil (Titular)", "type": "SELECT", "required": true, "options": ["Soltero", "Casado", "Divorciado", "Viudo"]},
      {"name": "NOMBRE_CONYUGE", "label": "Nombre del Cónyuge (Si aplica)", "type": "TEXT", "required": false},
      {"name": "NUM_DOC_CONYUGE", "label": "Documento Cónyuge", "type": "TEXT", "required": false},
      {"name": "DOMICILIO_TITULAR", "label": "Domicilio Legal", "type": "TEXT", "required": true},
      {"name": "NOMBRE_EMPRESA", "label": "Nombre de la Empresa (Sin E.I.R.L.)", "type": "TEXT", "required": true},
      {"name": "DISTRITO_EMPRESA", "label": "Distrito (Sede Empresa)", "type": "TEXT", "required": true},
      {"name": "PROVINCIA_EMPRESA", "label": "Provincia (Sede Empresa)", "type": "TEXT", "required": true},
      {"name": "DEPARTAMENTO_EMPRESA", "label": "Departamento (Sede Empresa)", "type": "TEXT", "required": true},
      {"name": "OBJETO_SOCIAL", "label": "Objeto Social (Completo)", "type": "TEXT", "required": true},
      {"name": "CAPITAL_NUMEROS", "label": "Capital (Números)", "type": "TEXT", "required": true},
      {"name": "CAPITAL_LETRAS", "label": "Capital (Letras)", "type": "TEXT", "required": true},
      {"name": "TIPO_APORTE", "label": "Tipo de Aporte Inicial", "type": "SELECT", "required": true, "options": ["BIENES DINERARIOS (Efectivo)", "BIENES NO DINERARIOS (Equipos/Bienes)"]},
      {"name": "NOMBRE_GERENTE", "label": "Nombre del Gerente", "type": "TEXT", "required": true},
      {"name": "NUM_DOC_GERENTE", "label": "Documento del Gerente", "type": "TEXT", "required": true},
      {"name": "CIUDAD_FIRMA", "label": "Ciudad Notarial", "type": "TEXT", "required": true},
      {"name": "DIA_FIRMA", "label": "Día de firma", "type": "TEXT", "required": true},
      {"name": "MES_FIRMA", "label": "Mes de firma", "type": "TEXT", "required": true},
      {"name": "ANIO_FIRMA", "label": "Últimos dos dígitos del año", "type": "TEXT", "required": true, "placeholder": "Ej: 26"},
      {"name": "CAL_ABOGADO", "label": "Registro Colegio Abogados (CAL)", "type": "TEXT", "required": false}
    ]',
    true
);
