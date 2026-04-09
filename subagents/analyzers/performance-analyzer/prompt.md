# Performance Analyzer Prompt

Analiza el performance del código en el School Management System.

## Métricas de Performance

### 1. Query Performance
- Detectar queries N+1
- Identificar queries sin índices
- Sugerir JOIN FETCH o @EntityGraph
- Analizar tiempo de ejecución

### 2. Memory Usage
- Detectar memory leaks potenciales
- Identificar objetos grandes en sesión
- Analizar uso de cache

### 3. Response Time
- Endpoints > 500ms requieren optimización
- Páginas > 2s requieren optimización
- Identificar bottlenecks

### 4. Database Connections
- Analizar uso del connection pool
- Detectar conexiones no liberadas
- Verificar configuración de HikariCP

## Optimizaciones Comunes para SMS
```java
// N+1 Fix
@EntityGraph(attributePaths = {"students", "teacher"})
List<Course> findAll();

// Batch Fetching
@BatchSize(size = 25)
@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
private List<Enrollment> enrollments;

// Caching
@Cacheable(value = "students", key = "#id")
public Student findById(Long id) { ... }

// Pagination
public Page<Student> findAll(Pageable pageable) { ... }
```

## Reporte de Salida
```markdown
## Performance Analysis Report

### Query Issues
| Location | Issue | Impact | Suggestion |
|----------|-------|--------|------------|
| ${file}:${line} | N+1 query | High | Use @EntityGraph |

### Response Time Issues
| Endpoint | Time (ms) | Threshold | Status |
|----------|-----------|-----------|--------|
| GET /students | 1200 | 500 | CRITICAL |

### Memory Issues
| Location | Issue | Impact |
|----------|-------|--------|
| ${file}:${line} | Large result set | Medium |

### Recommendations
1. Add @EntityGraph to ${method}
2. Add pagination to ${endpoint}
3. Enable caching for ${entity}
4. Add index on ${column}
```
