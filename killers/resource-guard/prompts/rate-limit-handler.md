# Resource Guard - Rate Limit Handler Prompt

## Manejo de Rate Limits para SMS

### Detección de Rate Limit
- HTTP 429 Too Many Requests
- Headers: X-RateLimit-Remaining, X-RateLimit-Reset
- Mensajes de error de API provider

### Estrategias de Manejo

#### 1. Backoff Exponencial
```
retry_delay = base_delay * (2 ^ attempt_number)
base_delay = 1 second
max_delay = 60 seconds
max_retries = 5
```

#### 2. Queue de Requests
- Encolar requests cuando se alcanza el límite
- Procesar en orden cuando se resetea el límite
- Priorizar requests críticos

#### 3. Fallback a Provider Alternativo
- Si OpenAI rate limits → usar Anthropic
- Si Anthropic rate limits → usar OpenAI
- Si ambos rate limits → esperar y reintentar

### Configuración por Provider
```yaml
openai:
  requests_per_minute: 60
  tokens_per_minute: 150000
  retry_after: 60

anthropic:
  requests_per_minute: 50
  tokens_per_minute: 100000
  retry_after: 60
```
