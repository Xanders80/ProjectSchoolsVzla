# Error Handler - Recovery Strategy Prompt

## Estrategias de Recuperación para SMS

### Auto-Fix Strategies

#### 1. Compilation Errors
- Missing imports → agregar imports automáticamente
- Deprecated methods → sugerir reemplazo (Java 21)
- Type mismatches → corregir tipos si es obvio
- Missing annotations → agregar anotaciones requeridas

#### 2. Test Failures
- Assertion failures → analizar expected vs actual
- Null pointer en tests → verificar mocks
- Transaction rollback → verificar @Transactional

#### 3. Build Failures
- Dependency conflicts → resolver versiones
- Plugin errors → verificar configuración
- Profile issues → verificar active profile

### Rollback Procedure
1. Identificar el último estado estable (git log)
2. Revertir cambios problemáticos
3. Verificar que el build pasa
4. Documentar la causa del fallo
5. Reintentar con correcciones

### Escalation Criteria
- Más de 3 intentos de auto-fix fallidos
- Error afecta múltiples módulos
- Error de seguridad identificado
- Pérdida potencial de datos
