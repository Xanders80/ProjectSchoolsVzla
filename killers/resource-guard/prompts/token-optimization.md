# Resource Guard - Token Optimization Prompt

## Optimización de Tokens para SMS

### Estrategias de Reducción

#### 1. Contexto Mínimo Necesario
- Solo incluir archivos relevantes a la tarea actual
- Resumir archivos largos en lugar de incluirlos completos
- Usar referencias en lugar de contenido completo

#### 2. Prompts Eficientes
- Ser específico y conciso en las instrucciones
- Usar templates predefinidos en lugar de descripciones largas
- Evitar redundancia en las instrucciones

#### 3. Respuestas Optimizadas
- Solicitar solo el código necesario, no explicaciones largas
- Usar diffs en lugar de archivos completos cuando sea posible
- Limitar el scope de la generación

### Cálculo de Tokens
```
input_tokens = system_prompt + context + user_request
output_tokens = generated_code + explanation
total_tokens = input_tokens + output_tokens

if total_tokens > warning_threshold:
    apply_optimization()
if total_tokens > critical_threshold:
    escalate_to_orchestrator()
```

### Presupuesto por Tipo de Tarea
- Code generation: max 8000 tokens
- Code review: max 6000 tokens
- Bug fix: max 10000 tokens
- Test generation: max 8000 tokens
- Architecture planning: max 12000 tokens
