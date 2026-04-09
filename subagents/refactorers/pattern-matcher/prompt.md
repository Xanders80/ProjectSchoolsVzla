# Pattern Matcher Prompt

Identifica patrones de diseño en el código del School Management System.

## Patrones a Detectar

### Patrones Implementados
- Repository Pattern (Spring Data JPA)
- Service Layer Pattern (Interface + Impl)
- DTO Pattern
- MVC Pattern (Controllers + Thymeleaf)
- Template Method (BaseDeleteController)
- Observer (ApplicationEventPublisher)
- Strategy (GradingScaleConverter)

### Patrones Sugeridos
- Specification Pattern para queries complejas
- Factory Pattern para creación de entidades
- Builder Pattern para objetos complejos
- Chain of Responsibility para validaciones

## Reporte de Salida
```markdown
## Pattern Analysis Report

### Detected Patterns
| Pattern | Location | Status |
|---------|----------|--------|
| Repository | All modules | ✅ Implemented |
| Service Layer | All modules | ✅ Implemented |
| DTO | academic, report | ⚠️ Partial |

### Missing Patterns
| Pattern | Where Needed | Benefit |
|---------|-------------|---------|
| Specification | Complex queries | Cleaner code |
| Factory | Entity creation | Consistency |

### Recommendations
1. Implement Specification pattern in ${repository}
2. Add Factory for ${entity} creation
3. Extract common logic to base class
```
