# Tech Lead - Technical Decisions Prompt

## Guía de Decisiones Técnicas para SMS

### Cuándo Escalar al Tech Lead
1. Cambios en la arquitectura de módulos
2. Nuevas dependencias en pom.xml
3. Cambios en SecurityConfig
4. Migraciones de base de datos breaking
5. Cambios en la estructura de paquetes
6. Nuevos filtros en el chain de seguridad
7. Modificaciones en el sistema de auditoría

### Criterios de Decisión

#### Nuevas Dependencias
- ¿Es realmente necesaria o se puede implementar con lo existente?
- ¿Es compatible con Spring Boot 3.5 y Java 21?
- ¿Tiene buena mantención y comunidad activa?
- ¿No introduce vulnerabilidades de seguridad?
- ¿El tamaño del dependency tree es razonable?

#### Cambios de Arquitectura
- ¿Mejora la mantenibilidad del código?
- ¿No rompe la compatibilidad con módulos existentes?
- ¿Es consistente con el patrón layered actual?
- ¿El beneficio justifica el costo de migración?

#### Decisiones de Base de Datos
- ¿La migración es reversible?
- ¿Se preservan los datos existentes?
- ¿El impacto en performance es aceptable?
- ¿Se consideran índices para queries frecuentes?

### Documentación de Decisiones
Cada decisión técnica importante debe documentarse como ADR:

```markdown
# ADR-XXX: [Título]

## Contexto
[Descripción del problema o situación]

## Decisión
[La decisión tomada]

## Consecuencias
- Positivas: [...]
- Negativas: [...]
- Neutrales: [...]

## Alternativas Consideradas
1. [Alternativa 1] - [Por qué no se eligió]
2. [Alternativa 2] - [Por qué no se eligió]
```
