# Context Manager - Priority Management Prompt

## Gestión de Prioridades de Contexto

### Niveles de Prioridad

#### P0 - Crítico (Siempre en contexto)
- Tarea actual en ejecución
- Archivos que se están editando
- Errores bloqueantes
- Configuración del módulo activo

#### P1 - Importante (Mantener activo)
- Tareas relacionadas inmediatas
- Archivos del mismo módulo
- Decisiones de la sesión actual
- Resultados de validación

#### P2 - Referencia (Mantener resumido)
- Documentación del proyecto
- Convenciones de código
- Patrones establecidos
- Historial reciente de cambios

#### P3 - Archivo (Descartar si necesario)
- Tareas completadas
- Errores resueltos
- Contexto de sesiones anteriores
- Información de módulos no relacionados

### Algoritmo de Gestión
1. Clasificar cada pieza de contexto por prioridad
2. Si el contexto > 80%, eliminar P3 primero
3. Si aún > 80%, resumir P2
4. Si aún > 80%, resumir P1 manteniendo solo lo esencial
5. Nunca eliminar P0 sin completar la tarea actual

### Project-Specific Priorities para SMS
- **P0:** Módulo actual (academic, finance, etc.), archivos en edición
- **P1:** Otros archivos del mismo módulo, SecurityConfig, pom.xml
- **P2:** Documentación de arquitectura, convenciones del proyecto
- **P3:** Sesiones anteriores, módulos no relacionados
