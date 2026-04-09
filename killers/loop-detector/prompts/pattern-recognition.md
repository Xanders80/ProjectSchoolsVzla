# Loop Detector - Pattern Recognition Prompt

## Detección de Patrones de Bucle Infinito

### Patrones a Detectar

#### 1. Outputs Idénticos
- Misma respuesta generada más de 3 veces consecutivas
- Mismos errores repetidos sin cambio de estrategia
- Mismo código generado con variaciones insignificantes

#### 2. Referencias Cíclicas
- Entity A referencia Entity B que referencia Entity A
- Service A inyecta Service B que inyecta Service A
- Controller llama Service que llama Controller

#### 3. Intentos Fallidos Repetidos
- Mismo test fallando más de 5 veces con el mismo fix
- Mismo error de compilación después de múltiples correcciones
- Build failure recurrente sin progreso

#### 4. Explosión de Tokens
- Contexto creciendo exponencialmente sin avance
- Respuestas cada vez más largas sin resolver el problema
- Token usage exceeding 50000 para una sola tarea

### Métricas de Detección
```
similarity_score = compare(current_output, previous_output)
if similarity_score > 0.95 and iterations > 3:
    TRIGGER_WARNING

if iterations > max_iterations (10):
    TRIGGER_TERMINATION

if time_without_progress > 300 seconds:
    TRIGGER_TERMINATION

if token_usage > 50000 per task:
    TRIGGER_WARNING
```

### Acciones
1. **Warning:** Notificar al orchestrator, sugerir cambio de enfoque
2. **Interruption:** Detener ejecución temporalmente, analizar causa
3. **Termination:** Detener completamente, escalar al Tech Lead
