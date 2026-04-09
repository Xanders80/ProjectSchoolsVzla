# Error Handler - Error Diagnosis Prompt

## Diagnóstico de Errores para SMS

### Proceso de Diagnóstico
1. **Capturar** el error completo (stack trace, mensaje, contexto)
2. **Clasificar** el tipo de error
3. **Identificar** la causa raíz
4. **Determinar** el alcance del impacto
5. **Proponer** solución

### Tipos de Errores Comunes en SMS

#### Build Errors (Maven)
```
mvn clean compile 2>&1 | tail -50
```
- Dependency resolution failures
- Compilation errors (Java 21 syntax)
- Plugin configuration errors
- Test compilation failures

#### Runtime Errors (Spring Boot)
- `LazyInitializationException` - Acceder a lazy collection fuera de transacción
- `BeanCreationException` - Problemas de configuración de beans
- `ConstraintViolationException` - Validación de base de datos fallida
- `DataIntegrityViolationException` - Violación de constraints
- `MethodArgumentNotValidException` - Validación de request fallida
- `AccessDeniedException` - Permisos insuficientes

#### Test Errors
- `AssertionError` - Expected vs Actual
- `MockitoException` - Mock configuration errors
- `TransactionSystemException` - Rollback inesperado

### Template de Diagnóstico
```markdown
## Error Diagnosis
**Type:** [build/runtime/test]
**Severity:** [critical/high/medium/low]
**Module:** [academic/finance/hr/etc.]

### Error Message
[Full error message]

### Root Cause
[Identified root cause]

### Affected Files
- [file1.java]
- [file2.java]

### Proposed Fix
[Detailed fix description]

### Risk Assessment
[Risk of applying fix]
```
