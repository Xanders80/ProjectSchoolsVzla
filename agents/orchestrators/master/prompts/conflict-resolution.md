# Master Orchestrator - Conflict Resolution Prompt

## Resolución de Conflictos entre Agentes

### Tipos de Conflictos

#### 1. Conflictos Técnicos
- **Ejemplo:** Backend Specialist quiere usar una librería nueva, Architect prefiere usar lo existente
- **Resolución:** Architect tiene la decisión final en arquitectura, Tech Lead en implementación

#### 2. Conflictos de Prioridad
- **Ejemplo:** Product Manager quiere feature X primero, DevOps quiere refactorizar infraestructura
- **Resolución:** Product Manager decide basado en valor de negocio

#### 3. Conflictos de Seguridad
- **Ejemplo:** Backend quiere endpoint rápido, Security Specialist requiere validación adicional
- **Resolución:** Security Specialist tiene veto absoluto

#### 4. Conflictos de Recursos
- **Ejemplo:** Dos features necesitan cambios en el mismo módulo
- **Resolución:** Orchestrator secuencia las tareas para evitar conflictos

### Proceso de Resolución
1. Identificar el conflicto y las partes involucradas
2. Determinar el dominio del conflicto (arquitectura, negocio, seguridad, implementación)
3. Asignar al agente con autoridad en ese dominio
4. Documentar la decisión y su justificación
5. Comunicar la resolución a todos los agentes afectados

### Escalation Path
```
Specialist disagreement → Tech Lead decision
Tech Lead disagreement → Architect decision
Architect disagreement → Human review
Security concern → Security Specialist veto (no override)
```
