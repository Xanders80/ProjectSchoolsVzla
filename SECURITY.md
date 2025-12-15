# Guía de Seguridad - Sistema de Gestión Escolar

## 🔒 Configuración de Seguridad

### Variables de Entorno Requeridas
```bash
# Base de datos
DB_URL=jdbc:mariadb://localhost:3306/dbSchollAdm
DB_USERNAME=root
DB_PASSWORD=your_secure_password

# Seguridad
COOKIE_SECURE=true  # Solo en HTTPS
SSL_ENABLED=true    # Para producción
SHOW_SQL=false      # Nunca true en producción
```

### Características de Seguridad Implementadas

#### ✅ Autenticación y Autorización
- Spring Security 6 con roles (ADMIN, TEACHER, STAFF, STUDENT, PARENT)
- Contraseñas encriptadas con BCrypt
- Sesiones con timeout de 30 minutos
- Control de sesiones concurrentes (máximo 1 por usuario)

#### ✅ Protección contra Ataques
- **CSRF Protection**: Habilitado con tokens
- **XSS Prevention**: Sanitización de entrada
- **SQL Injection**: Validación y sanitización
- **Rate Limiting**: 100 requests/minuto por IP
- **Headers de Seguridad**: HSTS, X-Frame-Options, Content-Type-Options

#### ✅ Auditoría y Monitoreo
- Logging de eventos de seguridad
- IDs únicos para errores
- Auditoría de cambios en entidades críticas
- Métricas con Actuator

### Configuración de Producción

1. **Variables de Entorno**:
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export DB_PASSWORD=your_secure_password
   export COOKIE_SECURE=true
   export SSL_ENABLED=true
   ```

2. **Base de Datos**:
   - Usar usuario con permisos mínimos
   - Configurar SSL para conexiones
   - Backups automáticos

3. **Servidor**:
   - Configurar HTTPS
   - Firewall configurado
   - Logs centralizados

### Endpoints de Monitoreo
- `/actuator/health` - Estado del sistema (público)
- `/actuator/metrics` - Métricas (solo ADMIN)
- `/actuator/info` - Información del sistema (solo ADMIN)

### Documentación API
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`