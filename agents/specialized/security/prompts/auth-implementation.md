# Security Specialist - Auth Implementation Prompt

## Implementación de Autenticación y Autorización SMS

### Configuración Actual de Seguridad
- **Authentication:** Form-based login con BCrypt
- **Authorization:** Role-based (ADMIN, STAFF, DIRECTOR, PARENT)
- **Session:** Max 1 concurrent session, session fixation protection
- **CSRF:** Habilitado (excepto H2 console)
- **Password Encoding:** BCryptPasswordEncoder

### Filtros en el Security Chain
1. `AnomalyDetectionFilter` - Detecta comportamiento anómalo
2. `RateLimitingFilter` - Rate limiting general
3. `DeleteRateLimitFilter` - Rate limiting para deletes
4. `AuditLoggingFilter` - Log de acciones de seguridad

### Implementar Nuevo Role
```java
// 1. Agregar al enum Role
public enum Role {
    ADMIN, STAFF, DIRECTOR, PARENT, TEACHER
}

// 2. Configurar en SecurityConfig
.requestMatchers("/teacher/**").hasAnyRole("ADMIN", "TEACHER")

// 3. Agregar en DataInitializer para seed
```

### Password Policy
```java
// Validación de contraseña
@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
private String password;
```

### Session Management
```java
.sessionManagement(session -> session
    .maximumSessions(1)
    .maxSessionsPreventsLogin(false)
    .sessionFixation().migrateSession()
    .invalidSessionUrl("/login?session=expired")
)
```

### Protección contra Brute Force
- Rate limiting en `/login`
- Contador de intentos fallidos
- Lockout temporal después de N intentos
- Logging de intentos fallidos
