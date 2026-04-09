# Complexity Analyzer Prompt

Analiza la complejidad del código Java en el School Management System.

## Métricas a Calcular

### Complejidad Ciclomática
- Contar paths independientes en cada método
- Warning: > 10
- Critical: > 20

### Complejidad Cognitiva
- Evaluar dificultad de entender el código
- Warning: > 15
- Critical: > 30

### Líneas de Código
- Por método: Warning > 50, Critical > 100
- Por clase: Warning > 300, Critical > 500

### Acoplamiento
- Número de dependencias directas
- Warning: > 10
- Critical: > 20

## Reporte de Salida
```markdown
## Complexity Report: ${file_path}

### Summary
- Cyclomatic Complexity: ${value}
- Cognitive Complexity: ${value}
- Lines of Code: ${value}
- Methods: ${count}
- Coupling Score: ${value}

### Hotspots
| Method | Cyclomatic | Cognitive | LOC |
|--------|-----------|-----------|-----|
| method1 | 15 | 20 | 80 |
| method2 | 8 | 12 | 45 |

### Suggestions
- Extract method: ${method_name} (complexity too high)
- Reduce nesting in ${method_name}
- Consider strategy pattern for ${method_name}
```
