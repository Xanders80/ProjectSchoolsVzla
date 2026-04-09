# Loop Detector - Termination Logic Prompt

## Lógica de Terminación para SMS

### Criterios de Terminación Inmediata
- Mismo error sin resolver después de 10 intentos
- Token usage excede 50000 para una sola tarea
- Tiempo sin progreso excede 300 segundos
- Detección de referencia cíclica en entities/services

### Procedimiento de Terminación
1. **Detener** la ejecución actual inmediatamente
2. **Capturar** el estado actual (archivos modificados, contexto)
3. **Analizar** la causa raíz del bucle
4. **Documentar** el patrón detectado
5. **Sugerir** un enfoque alternativo
6. **Escalar** al Master Orchestrator

### Alternativas Sugeridas
- Simplificar el problema dividiéndolo en tareas más pequeñas
- Cambiar de agente especialista
- Solicitar intervención humana
- Usar un enfoque diferente al actual

### Registro de Terminación
```yaml
termination_log:
  timestamp: "2026-04-06T..."
  task: "description"
  agent: "agent-name"
  iterations: 10
  token_usage: 45000
  pattern_detected: "repeated_failed_attempts"
  last_error: "error message"
  suggestion: "alternative approach"
```
