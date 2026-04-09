# Backend Specialist - Optimization Prompt

## Optimización de Backend SMS

### JPA/Hibernate Performance

#### Evitar N+1 Queries
```java
// MAL: N+1 queries
List<Course> courses = courseRepository.findAll();
for (Course c : courses) {
    c.getStudents().size(); // Trigger lazy load por cada curso
}

// BIEN: JOIN FETCH
@Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students WHERE c.deleted = false")
List<Course> findAllWithStudents();

// BIEN: @EntityGraph
@EntityGraph(attributePaths = {"students", "teacher"})
List<Course> findAll();
```

#### Batch Fetching
```java
@BatchSize(size = 25)
@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
private List<Enrollment> enrollments;
```

#### Projections para Queries Específicos
```java
// DTO Projection
@Query("SELECT new com.school.academic.dto.StudentAttendanceStatsDTO(s.id, s.fullName, COUNT(a)) " +
       "FROM Student s LEFT JOIN Attendance a ON s.id = a.student.id GROUP BY s.id")
List<StudentAttendanceStatsDTO> getAttendanceStats();
```

### Caching
```java
@Cacheable(value = "students", key = "#id")
public Student findById(Long id) { ... }

@CacheEvict(value = "students", key = "#id")
public void update(Long id, Student student) { ... }
```

### Paginación
```java
// Siempre usar paginación para listas
public Page<Student> findAll(Pageable pageable) {
    return studentRepository.findByDeletedFalse(pageable);
}
```

### Database Optimization
- Índices en foreign keys usadas en joins
- Índices en campos de búsqueda frecuente
- Composite indexes para queries multi-columna
- Evitar SELECT * en queries grandes
- Usar EXPLAIN ANALYZE para analizar queries lentos

### API Performance
- Response time objetivo: < 500ms para APIs, < 2s para páginas
- Compression habilitada en application.properties
- Connection pool configurado (HikariCP por defecto)
- Timeouts configurados para operaciones externas

### Logging Optimizado
```java
// Usar SLF4J con lazy evaluation
log.debug("Processing student: {}", () -> student.getExpensiveToString());

// Niveles apropiados
log.error() - Errores que requieren atención
log.warn()  - Advertencias no críticas
log.info()  - Eventos significativos
log.debug() - Información de debugging
```
