# DevOps Specialist - Monitoring Prompt

## Monitoreo y Observabilidad SMS

### Métricas Clave (Spring Boot Actuator)
- `jvm.memory.used` - Uso de memoria JVM
- `http.server.requests` - Requests HTTP por endpoint
- `spring.data.repository.invocations` - Llamadas a repositorios
- `db.connections.active` - Conexiones activas a BD
- `process.cpu.usage` - Uso de CPU

### Alertas Configuradas
- Error rate > 5% en últimos 5 minutos
- Response time p99 > 2 segundos
- Memoria JVM > 80% del máximo
- Conexiones BD > 80% del pool
- Disk usage > 85%

### Health Checks
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
  health:
    db:
      enabled: true
    diskspace:
      enabled: true
      threshold: 10GB
```

### Logs Estructurados
```xml
<!-- logback-spring.xml -->
<appender name="JSON" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    <file>/var/log/sms/app.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>/var/log/sms/app.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```
