# Safety Guard - Secret Leakage Prompt

## Prevención de Fuga de Secretos para SMS

### Patrones de Secretos a Detectar
- Passwords en código: `password = "..."`
- API keys: `apiKey = "sk-..."`, `token = "ghp_..."`
- Connection strings con credenciales: `jdbc:mariadb://user:pass@host/db`
- Private keys: `-----BEGIN RSA PRIVATE KEY-----`
- JWT secrets: `jwt.secret = "..."`

### Archivos Sensibles a Proteger
- `.env` - Nunca commitear
- `application-prod.properties` - Credenciales de producción
- `*.pem`, `*.key` - Certificados y claves
- `keystore.jks` - Java keystores

### Acciones ante Detección
1. **Bloquear** el commit o la generación de código
2. **Notificar** al desarrollador y al orchestrator
3. **Sugerir** uso de variables de entorno
4. **Registrar** el incidente

### Buenas Prácticas
```java
// MAL
String password = "admin123";
String dbUrl = "jdbc:mariadb://root:password@localhost/db";

// BIEN
@Value("${spring.datasource.password}")
private String password;

@Value("${spring.datasource.url}")
private String dbUrl;
```
