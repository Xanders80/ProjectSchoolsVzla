# Security Analyzer Prompt

Analiza vulnerabilidades de seguridad en el School Management System.

## Checks de Seguridad

### 1. SQL Injection
- Verificar uso de prepared statements (JPA lo hace automáticamente)
- Detectar queries nativas con concatenación de strings
- Verificar InputSanitizer en inputs de usuario

### 2. XSS Prevention
- Verificar escape de output en Thymeleaf (automático con th:text)
- Detectar uso de th:utext con datos de usuario
- Verificar InputSanitizer

### 3. CSRF Protection
- Verificar CSRF habilitado en SecurityConfig
- Detectar formularios sin token CSRF
- Verificar exclusión de rutas CSRF

### 4. Authentication/Authorization
- Verificar @PreAuthorize en todos los endpoints
- Detectar endpoints sin protección
- Verificar configuración de roles en SecurityConfig

### 5. Secret Exposure
- Detectar credenciales hardcodeadas
- Verificar uso de variables de entorno
- Detectar logs con datos sensibles

### 6. Input Validation
- Verificar @Valid en controllers
- Detectar campos sin validación
- Verificar grupos de validación

## Reporte de Salida
```markdown
## Security Analysis Report

### Critical Vulnerabilities
| Location | Type | Description | Fix |
|----------|------|-------------|-----|
| ${file}:${line} | SQL Injection | String concatenation in query | Use @Query with parameters |

### High Severity
| Location | Type | Description | Fix |
|----------|------|-------------|-----|
| ${file}:${line} | Missing Auth | Endpoint without @PreAuthorize | Add @PreAuthorize |

### Medium Severity
| Location | Type | Description | Fix |
|----------|------|-------------|-----|
| ${file}:${line} | XSS Risk | th:utext with user data | Use th:text |

### Recommendations
1. Add @PreAuthorize to ${endpoint}
2. Use parameterized query in ${method}
3. Move credentials to environment variables
4. Add input validation to ${field}
```
