# Product Manager - Acceptance Criteria Prompt

## Definición de Criterios de Aceptación para SMS

### Tipos de Criterios

#### 1. Funcionales
- Operaciones CRUD completas
- Validaciones de negocio
- Flujos de trabajo completos
- Integraciones entre módulos

#### 2. No Funcionales
- Performance: tiempos de respuesta < 2s para páginas, < 500ms para APIs
- Seguridad: protección CSRF, rate limiting, sanitización XSS
- Internacionalización: mensajes en inglés y español
- Accesibilidad: navegación por teclado, contraste adecuado

#### 3. Específicos del SMS
- **Auditoría:** Las acciones críticas deben registrarse en AuditLog
- **Soft Delete:** Las eliminaciones deben ser lógicas, no físicas
- **Roles:** Cada endpoint debe verificar el rol del usuario
- **Validación:** Usar grupos ValidationGroups.Create/Update
- **Caching:** Datos frecuentes deben estar cacheados

### Formato de Criterios

```markdown
### Criterios de Aceptación

#### Funcionales
- [ ] El usuario puede crear un nuevo [entidad] con todos los campos requeridos
- [ ] El sistema valida [regla de negocio] y muestra mensaje de error apropiado
- [ ] Los datos se persisten correctamente en MariaDB
- [ ] La operación se registra en el log de auditoría

#### Validaciones
- [ ] Campos obligatorios validados con @NotBlank/@NotNull
- [ ] Formatos validados (@Email, @Pattern)
- [ ] Restricciones de unicidad verificadas
- [ ] Mensajes de error en idioma del usuario (en/es)

#### Seguridad
- [ ] Endpoint protegido con @PreAuthorize o configuración SecurityConfig
- [ ] CSRF token incluido en formularios
- [ ] Input sanitizado contra XSS
- [ ] Rate limiting aplicado si es operación sensible

#### UI/UX
- [ ] Formulario con validación client-side (validation.js)
- [ ] Mensajes de éxito/error con clases Bootstrap
- [ ] Tabla con DataTables (búsqueda, paginación, ordenamiento)
- [ ] Responsive con Bootstrap 4
```
