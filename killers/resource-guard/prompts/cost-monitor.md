# Resource Guard - Cost Monitoring Prompt

## Monitoreo de Costos de API para SMS

### Métricas a Monitorear
- **Tokens por tarea:** Contar input + output tokens
- **Costo por agente:** Trackear gasto por tipo de agente
- **Costo diario:** Total acumulado en el día
- **Costo por workflow:** Total por flujo de trabajo completo

### Umbrales de Costo
```yaml
warning:
  - task_cost > $0.50
  - daily_cost > $10.00
  - token_count > 30000

critical:
  - task_cost > $1.00
  - daily_cost > $20.00
  - token_count > 50000

block:
  - task_cost > $2.00
  - daily_cost > $25.00
  - token_count > 80000
```

### Estrategias de Optimización
1. **Context Pruning:** Eliminar contexto innecesario antes de enviar
2. **Response Truncation:** Limitar tamaño de respuestas
3. **Batch Requests:** Agrupar múltiples preguntas en una sola
4. **Cache Responses:** Reutilizar respuestas similares
5. **Model Selection:** Usar modelos más baratos para tareas simples

### Alertas
- Notificar al orchestrator cuando se alcanza el 75% del presupuesto
- Bloquear nuevas tareas cuando se alcanza el 100%
- Generar reporte de uso al final del día
