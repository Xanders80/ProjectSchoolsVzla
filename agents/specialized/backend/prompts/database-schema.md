# Backend Specialist - Database Schema Prompt

## Diseño de Esquemas de Base de Datos para SMS

### Convenciones de Nomenclatura
- Tablas: `snake_case` plural (e.g., `students`, `academic_periods`)
- Columnas: `snake_case` (e.g., `first_name`, `created_at`)
- Foreign keys: `fk_tabla_columna` (e.g., `fk_students_section_id`)
- Índices: `idx_tabla_columna` (e.g., `idx_students_enrollment_date`)

### Campos Estándar en Todas las Entidades
```sql
CREATE TABLE example_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP NULL
);
```

### Relaciones JPA
```java
// One-to-Many (padre → hijos)
@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Enrollment> enrollments;

// Many-to-One (hijo → padre)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "course_id", nullable = false)
private Course course;

// Many-to-Many (con tabla intermedia)
@ManyToMany
@JoinTable(
    name = "student_courses",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id")
)
private Set<Course> courses;
```

### Migraciones
- Usar archivos `.sql` en `src/main/resources/db/migration/`
- Nomenclatura: `V{version}__description.sql` (e.g., `V001__create_students_table.sql`)
- Siempre incluir rollback en comentarios
- Probar en H2 antes de aplicar en MariaDB

### Índices Recomendados
- Foreign keys frecuentes en queries
- Campos de búsqueda (nombre, código, email)
- Campos de filtrado (estado, fecha, rol)
- Composite indexes para queries con múltiples condiciones
