# Architect - Architecture Review Prompt

Cuando revises decisiones arquitectónicas, evalúa:

## Criterios de Revisión

### 1. Coherencia con la Arquitectura Existente
- ¿Sigue el patrón layered del proyecto?
- ¿Respeta las boundaries entre módulos?
- ¿Mantiene la separación web/domain/data?
- ¿Los controllers permanecen en `com.school.web.controller.{domain}`?

### 2. Diseño de Base de Datos
- ¿Las entities siguen las convenciones de nomenclatura?
- ¿Se implementa soft delete correctamente?
- ¿Las relaciones JPA están bien definidas?
- ¿Se considera el impacto en queries existentes?
- ¿Las migraciones son reversibles?

### 3. Seguridad
- ¿Los nuevos endpoints tienen protección adecuada?
- ¿Se respetan los roles existentes (ADMIN, STAFF, DIRECTOR, PARENT)?
- ¿Se aplica input sanitization?
- ¿Se consideran rate limits para nuevas operaciones?

### 4. Performance
- ¿Se utiliza caching donde es apropiado?
- ¿Las queries N+1 están evitadas?
- ¿Se considera paginación para listas grandes?
- ¿Los índices de base de datos son adecuados?

### 5. Mantenibilidad
- ¿El código sigue los patrones establecidos?
- ¿Los nombres son descriptivos y consistentes?
- ¿La complejidad ciclomática es aceptable?
- ¿Se pueden escribir pruebas unitarias fácilmente?

### 6. Internacionalización
- ¿Los mensajes de usuario están externalizados?
- ¿Se consideran ambos idiomas (en/es)?

## Checklist de Aprobación
- [ ] Arquitectura coherente con el proyecto
- [ ] Base de datos bien diseñada
- [ ] Seguridad implementada
- [ ] Performance considerado
- [ ] Código mantenible
- [ ] Internacionalización incluida
