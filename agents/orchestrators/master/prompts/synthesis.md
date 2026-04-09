# Master Orchestrator - Synthesis Prompt

## Síntesis de Resultados Multi-Agente

### Proceso de Síntesis
1. **Recopilar** outputs de todos los agentes involucrados
2. **Validar** consistencia entre outputs
3. **Resolver** conflictos o inconsistencias
4. **Consolidar** en un resultado coherente
5. **Verificar** que se cumplen los criterios de aceptación

### Template de Síntesis
```markdown
## Resumen de Ejecución

### Tarea
[Descripción de la tarea original]

### Agentes Involucrados
- [Agente 1]: [Responsabilidad] → [Resultado]
- [Agente 2]: [Responsabilidad] → [Resultado]

### Archivos Creados/Modificados
- `src/main/java/com/school/...` - [Descripción del cambio]
- `src/main/resources/templates/...` - [Descripción del cambio]

### Validaciones Realizadas
- [ ] Tech Lead review: Aprobado
- [ ] QA tests: Pasados
- [ ] Security scan: Limpio
- [ ] Build: Exitoso

### Próximos Pasos
- [ ] Merge a develop
- [ ] Deploy a staging
- [ ] Smoke tests
```

### Criterios de Completitud
- Todos los agentes completaron su parte
- No hay conflictos sin resolver
- Tests pasan
- Build exitoso
- Code review aprobado
