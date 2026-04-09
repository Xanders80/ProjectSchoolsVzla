// Docker Generator - DevOps Tool for SMS
// Generates Dockerfile and docker-compose configurations

export interface DockerConfig {
  appName: string;
  javaVersion: string;
  port: number;
  profiles: string[];
  database: {
    image: string;
    name: string;
    port: number;
    rootPassword: string;
  };
}

export class DockerGenerator {
  generateDockerfile(config: DockerConfig): string {
    return `FROM eclipse-temurin:${config.javaVersion}-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE ${config.port}
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \\
  CMD wget -qO- http://localhost:${config.port}/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}"]
`;
  }

  generateDockerCompose(config: DockerConfig): string {
    return `version: '3.8'
services:
  app:
    build: .
    container_name: ${config.appName}
    ports:
      - "${config.port}:${config.port}"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:mariadb://db:${config.database.port}/${config.database.name}
      - DB_USERNAME=root
      - DB_PASSWORD=${config.database.rootPassword}
    depends_on:
      db:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - sms-network

  db:
    image: ${config.database.image}
    container_name: ${config.appName}-db
    environment:
      MYSQL_ROOT_PASSWORD: ${config.database.rootPassword}
      MYSQL_DATABASE: ${config.database.name}
    ports:
      - "${config.database.port}:${config.database.port}"
    volumes:
      - mariadb_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped
    networks:
      - sms-network

volumes:
  mariadb_data:

networks:
  sms-network:
    driver: bridge
`;
  }

  generateNginxConfig(domain: string, port: number): string {
    return `server {
    listen 80;
    server_name ${domain};
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name ${domain};

    ssl_certificate /etc/letsencrypt/live/${domain}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${domain}/privkey.pem;

    location / {
        proxy_pass http://localhost:${port};
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
`;
  }
}
