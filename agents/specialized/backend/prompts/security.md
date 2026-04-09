# Backend Specialist - Security Prompt

## Seguridad en el Backend SMS

### Spring Security Configuration
- Configurar en `SecurityConfig.java` del módulo `core`
- Definir patrones de URL por rol en `http.authorizeHttpRequests()`
- Habilitar CSRF para todas las rutas excepto APIs internas
- Configurar session management con concurrencia máxima

### Filtros de Seguridad Existentes
1. `AnomalyDetectionFilter` - Detecta comportamiento anómalo
2. `RateLimitingFilter` - Rate limiting general
3. `DeleteRateLimitFilter` - Rate limiting específico para deletes
4. `AuditLoggingFilter` - Log de acciones de seguridad

### Protección de Endpoints
```java
// En SecurityConfig
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/students/**", "/sections/**").hasAnyRole("ADMIN", "STAFF")
.requestMatchers("/reports/**").hasAnyRole("ADMIN", "DIRECTOR")
.requestMatchers("/health/**", "/hr/**", "/bi/**").hasAnyRole("ADMIN", "DIRECTOR")
.requestMatchers("/portal/**").hasAnyRole("PARENT", "ADMIN")
```

### Method-Level Security
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteStudent(Long id) { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public List<Student> findAll() { ... }
```

### Input Sanitization
```java
// Usar InputSanitizer para prevenir XSS
String sanitized = InputSanitizer.sanitize(userInput);
```

### Validación de Negocio
```java
// Usar BusinessValidationException para reglas de negocio
if (student.getEnrollmentDate().isAfter(academicPeriod.getEndDate())) {
    throw new BusinessValidationException("enrollment.date.invalid");
}
```

### Secretos y Configuración
- Nunca hardcodear credenciales
- Usar variables de entorno para datos sensibles
- Variables en `.env` y `application-{profile}.properties`
- Rotar contraseñas periódicamente

### Headers de Seguridad
- X-Frame-Options: DENY
- HSTS: max-age=31536000; includeSubDomains
- Content-Security-Policy restrictiva
- X-Content-Type-Options: nosniff
