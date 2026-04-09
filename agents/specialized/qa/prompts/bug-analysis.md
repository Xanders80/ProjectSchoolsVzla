# QA Specialist - Bug Analysis Prompt

## Análisis de Bugs para SMS

### Clasificación de Bugs
- **Critical:** Pérdida de datos, security breach, sistema caído
- **High:** Feature principal no funciona, workaround difícil
- **Medium:** Feature secundaria no funciona, workaround disponible
- **Low:** Cosmetic, typo, mejora menor

### Template de Bug Report
```markdown
## Bug: [Título descriptivo]
**Módulo:** [academic/finance/hr/etc.]
**Severidad:** [Critical/High/Medium/Low]
**Prioridad:** [P1/P2/P3/P4]

### Pasos para Reproducir
1. Ir a `/ruta`
2. Click en botón X
3. Completar campo Y con valor Z
4. Submit

### Comportamiento Esperado
[Qué debería pasar]

### Comportamiento Actual
[Qué pasa realmente]

### Environment
- Browser: Chrome 120
- Profile: dev/prod
- Database: MariaDB/H2

### Screenshots/Logs
[Adjuntar evidencia]

### Root Cause Analysis
[Causa raíz identificada]

### Fix Suggestion
[Sugerencia de corrección]
```

### Patrones Comunes de Bugs en SMS
1. **N+1 Queries:** Performance degradation con datasets grandes
2. **LazyInitializationException:** Acceder a lazy collections fuera de transacción
3. **CSRF Token Missing:** Formularios sin token CSRF
4. **Soft Delete Ignored:** Queries que no filtran `deleted = false`
5. **Role Check Missing:** Endpoints sin `@PreAuthorize`
6. **i18n Key Missing:** Textos hardcodeados en lugar de `#{key}`
7. **Validation Bypass:** Falta `@Valid` en controller methods
