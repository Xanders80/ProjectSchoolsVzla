# Architect - Planning Prompt

Cuando recibas una tarea para planificar, sigue este proceso:

## 1. Análisis de Impacto
- Identifica qué módulos del SMS se ven afectados
- Determina dependencies entre módulos (academic → schedule, finance → academic, etc.)
- Evalúa impacto en la base de datos (nuevas tablas, modificaciones, migraciones)
- Considera implicaciones de seguridad (nuevos roles, permisos, endpoints)

## 2. Diseño de Solución
- Define la estructura de paquetes siguiendo la convención del proyecto
- Especifica entities, repositories, services, controllers necesarios
- Diseña el esquema de base de datos con relaciones y constraints
- Define DTOs si la transferencia de datos es compleja

## 3. Plan de Implementación
- Ordena las tareas: entities → repositories → services → controllers → vistas
- Identifica tareas paralelizables (frontend/backend pueden ir en paralelo)
- Define puntos de integración entre módulos
- Especifica pruebas necesarias

## 4. Consideraciones Específicas del SMS
- ¿El cambio afecta el sistema de auditoría?
- ¿Requiere nuevos roles o permisos?
- ¿Necesita internacionalización (en/es)?
- ¿Afecta el caching existente?
- ¿Requiere migración de base de datos?
- ¿Impacta los scheduled jobs existentes?

## Output Esperado
Genera un documento de especificación técnica con:
- Diagrama de componentes afectado
- Lista de archivos a crear/modificar
- Esquema de base de datos (DDL si aplica)
- Plan de implementación ordenado
- Riesgos y mitigaciones
