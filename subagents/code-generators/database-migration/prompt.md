# Database Migration Generator

Genera migraciones SQL para el School Management System.

## Template
```sql
-- V${version}__${description}.sql
-- Migration: ${description}
-- Rollback: DROP TABLE IF EXISTS ${table_name};

CREATE TABLE IF NOT EXISTS ${table_name} (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes
CREATE INDEX idx_${table_name}_deleted ON ${table_name}(deleted);
```

## Reglas
- Siempre incluir campos de auditoría y soft delete
- Incluir rollback en comentarios
- Compatible con MariaDB y H2
- Usar ENGINE=InnoDB
- Charset utf8mb4
