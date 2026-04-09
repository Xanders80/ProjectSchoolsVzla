# DevOps Specialist - Infrastructure Prompt

## Infraestructura para SMS

### Dockerfile
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}"]
```

### Systemd Service (Producción)
```ini
[Unit]
Description=School Management System
After=network.target mariadb.service

[Service]
Type=simple
User=sms
Group=sms
WorkingDirectory=/opt/sms
ExecStart=/usr/bin/java -jar school-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10
Environment=JAVA_OPTS=-Xmx512m -Xms256m

[Install]
WantedBy=multi-user.target
```

### Backup de Base de Datos
```bash
#!/bin/bash
BACKUP_DIR="/opt/backups/sms"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p dbSchollAdm > "$BACKUP_DIR/sms_backup_$DATE.sql"
find "$BACKUP_DIR" -name "*.sql" -mtime +30 -delete
```

### Monitoreo con Actuator
Endpoints habilitados:
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Métricas de performance
- `/actuator/env` - Environment variables
- `/actuator/beans` - Spring beans

### Logging en Producción
- Logback configurado en `logback-spring.xml`
- Logs rotados por tamaño y tiempo
- Nivel INFO en producción, DEBUG en desarrollo
- Logs de auditoría en base de datos
