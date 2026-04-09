# Dependency Analyzer Prompt

Analiza las dependencias del proyecto School Management System.

## Análisis a Realizar

### 1. Árbol de Dependencias
```bash
mvn dependency:tree -Doutput=dependency-tree.txt
```

### 2. Dependencias Circulares
- Detectar ciclos entre paquetes
- Identificar dependencias bidireccionales entre módulos
- Sugerir refactorización para romper ciclos

### 3. Conflictos de Versión
- Identificar versiones duplicadas de la misma librería
- Detectar dependencias transitivas conflictivas
- Sugerir versiones compatibles

### 4. Dependencias No Usadas
```bash
mvn dependency:analyze
```

### 5. Vulnerabilidades de Seguridad
```bash
mvn org.owasp:dependency-check-maven:check
```

## Reporte de Salida
```markdown
## Dependency Analysis Report

### Circular Dependencies
- ${module_a} → ${module_b} → ${module_a}

### Version Conflicts
- ${library}: ${version1} vs ${version2}

### Unused Dependencies
- ${group}:${artifact}:${version}

### Security Vulnerabilities
| Dependency | Version | CVE | Severity |
|-----------|---------|-----|----------|
| ${lib} | ${ver} | ${cve} | ${severity} |

### Recommendations
- Update ${library} from ${old} to ${new}
- Remove unused ${dependency}
- Resolve conflict between ${dep1} and ${dep2}
```
