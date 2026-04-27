# Documentación Técnica - Legit Backend

Esta documentación profundiza en el funcionamiento interno de los subsistemas más complejos de la plataforma AbogHub.

## 1. Sistema de Autenticación y Seguridad

El sistema utiliza **Spring Security** con una implementación de **JWT Stateless**.

### Flujo de Autenticación:
1. El cliente envía credenciales a `/api/v1/auth/login`.
2. `JwtService` genera un token firmado con HMAC-512.
3. Cada petición subsiguiente debe incluir el header `Authorization: Bearer <token>`.
4. `JwtAuthenticationFilter` intercepta la petición, valida el token y establece el `SecurityContext`.

### Roles Disponibles:
- `CLIENT`: Acceso a dashboard de usuario, marketplace y gestión de sus propios casos.
- `LAWYER`: Acceso a gestión de perfiles profesionales, agenda y propuestas legales.
- `ADMIN`: Control total sobre usuarios, reportes y configuraciones del sistema.

## 2. Motor de IA y Asistente Legal (LangChain4j)

El sistema integra inteligencia artificial para asistir en tareas legales:

- **RAG (Retrieval-Augmented Generation)**: Los documentos subidos por los abogados se vectorizan y almacenan en **PgVector**. Cuando se hace una consulta, la IA busca el contexto más relevante antes de generar una respuesta.
- **Limpieza de Sesiones**: `AiSessionCleanupTask` se encarga de liberar memoria de conversaciones inactivas para optimizar el rendimiento del servidor.

## 3. Gestión Documental y Generación de PDF

La plataforma no solo almacena archivos, sino que los genera dinámicamente:

- **Plantillas**: Se utilizan fragmentos HTML procesados por **Thymeleaf**.
- **Conversión**: **OpenHTMLtoPDF** transforma el HTML resultante en documentos PDF profesionales.
- **Almacenamiento**: Integración nativa con **Cloudinary** para el manejo de assets y streaming seguro de documentos.

## 4. Sistema de Citas y Disponibilidad

El módulo `appointment` resuelve el problema de los choques de horario:
- **Validación de Solapamiento**: El repositorio usa una query personalizada (`hasOverlappingAppointments`) para verificar si un abogado ya tiene un compromiso en un rango de tiempo dado.
- **Normalización**: Todas las fechas se manejan en `OffsetDateTime` para asegurar la coherencia entre el cliente (local) y el servidor (UTC).

## 5. Base de Datos y Migraciones

Se utiliza **PostgreSQL** con **Flyway** para el control de versiones del esquema:
- Los scripts se encuentran en `src/main/resources/db/migration`.
- Es imperativo **no modificar** archivos SQL existentes; siempre crear una nueva versión (`VXX__descripcion.sql`).

---

## 📈 Recomendaciones de Mantenimiento

- **Logs**: Configurados para rotar y mostrar niveles `DEBUG` solo en el paquete de seguridad durante desarrollo.
- **Escalabilidad**: El diseño stateless permite horizontal scaling mediante balanceadores de carga sin necesidad de sticky sessions.
