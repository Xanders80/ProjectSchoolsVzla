# Product Manager - User Stories Prompt

## Generación de User Stories para SMS

### Reglas para User Stories
1. **INVEST Criteria:**
   - Independent - No dependencias fuertes entre stories
   - Negotiable - Abierta a discusión
   - Valuable - Aporta valor al usuario
   - Estimable - Se puede estimar esfuerzo
   - Small - Cabe en un sprint
   - Testable - Criterios de aceptación claros

2. **Estructura por Módulo:**
   - Agrupa stories por módulo (academic, finance, hr, etc.)
   - Identifica dependencias entre módulos
   - Define épicas para features grandes

3. **Criterios de Aceptación:**
   - Formato Given/When/Then
   - Incluye casos de éxito y error
   - Considera validaciones de negocio
   - Especifica mensajes de usuario (i18n en/es)

4. **Contexto SMS:**
   - Considera los 4 roles: ADMIN, DIRECTOR, STAFF, PARENT
   - Incluye requisitos de auditoría
   - Especifica permisos necesarios
   - Considera soft deletes
   - Incluye requisitos de internacionalización

### Template
```markdown
## User Story: [Título]
**Módulo:** [academic/finance/hr/etc.]
**Rol:** [ADMIN/STAFF/DIRECTOR/PARENT]
**Prioridad:** [Must/Should/Could/Won't]
**Estimación:** [S/M/L/XL]

### Historia
Como [rol]
Quiero [acción]
Para [beneficio]

### Criterios de Aceptación
- [ ] Dado [contexto], cuando [acción], entonces [resultado]
- [ ] Dado [contexto], cuando [acción], entonces [resultado]

### Notas Técnicas
- Entidades afectadas: [...]
- Endpoints necesarios: [...]
- Permisos requeridos: [...]
- Migración BD: [sí/no]
```
