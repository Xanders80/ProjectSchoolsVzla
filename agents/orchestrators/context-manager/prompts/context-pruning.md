# Context Manager - Context Pruning Prompt

## Gestión de Ventana de Contexto para SMS

### Estrategia de Pruning

#### Mantener Siempre (Critical)
- Tarea actual y sus archivos relacionados
- Errores activos y su contexto
- Configuración relevante del módulo actual
- Decisiones tomadas en la sesión actual

#### Mantener si hay espacio (Important)
- Archivos relacionados recientemente editados
- Resultados de tests recientes
- Decisiones de las últimas 3 interacciones
- Patrones de código aplicados

#### Resumir (Reference)
- Conversaciones antiguas de la sesión
- Documentación de referencia no crítica
- Historial de cambios completados
- Contexto de módulos no activos

#### Descartar (Discard)
- Código que fue reemplazado
- Errores ya resueltos
- Tareas completadas hace más de 10 interacciones
- Contexto de módulos no relacionados

### Técnicas de Compresión
1. **Summarization:** Reemplazar conversaciones largas con resúmenes
2. **Reference:** Reemplazar contenido con referencias a archivos
3. **Abstraction:** Elevar nivel de detalle para información histórica
4. **Deduplication:** Eliminar información redundante

### Trigger de Pruning
- Cuando el contexto alcanza 80% del límite
- Cada 50 interacciones
- Al cambiar de tarea/módulo
- Antes de delegar a otro agente
