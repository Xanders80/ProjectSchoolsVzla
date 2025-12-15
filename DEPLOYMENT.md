# Guía de Despliegue - Sistema de Gestión Escolar

## 🚀 Despliegue en Producción

### Prerrequisitos
- Java 21 LTS
- MariaDB 10.6+
- Servidor web con HTTPS

### Pasos de Despliegue

#### 1. Preparación del Entorno
```bash
# Crear usuario de base de datos
CREATE USER 'school_user'@'localhost' IDENTIFIED BY 'secure_password';
CREATE DATABASE dbSchollAdm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT SELECT, INSERT, UPDATE, DELETE ON dbSchollAdm.* TO 'school_user'@'localhost';
```

#### 2. Variables de Entorno
```bash
# Crear archivo .env
cat > .env << EOF
DB_URL=jdbc:mariadb://localhost:3306/dbSchollAdm
DB_USERNAME=school_user
DB_PASSWORD=secure_password
COOKIE_SECURE=true
SSL_ENABLED=true
SPRING_PROFILES_ACTIVE=prod
EOF
```

#### 3. Compilación
```bash
./mvnw clean package -DskipTests
```

#### 4. Ejecución
```bash
# Con variables de entorno
source .env
java -jar target/school-management-0.0.1-SNAPSHOT.jar

# O directamente
java -jar target/school-management-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.password=secure_password
```

### Configuración de Nginx (Opcional)
```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Monitoreo
- Health Check: `https://your-domain.com/actuator/health`
- Logs: `tail -f logs/school-management.log`
- Métricas: `https://your-domain.com/actuator/metrics`

### Backup
```bash
# Backup de base de datos
mysqldump -u school_user -p dbSchollAdm > backup_$(date +%Y%m%d).sql

# Backup de logs
tar -czf logs_backup_$(date +%Y%m%d).tar.gz logs/
```