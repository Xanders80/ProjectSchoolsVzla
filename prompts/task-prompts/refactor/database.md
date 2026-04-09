# Refactor Database Prompt

Refactoriza el esquema de base de datos del SMS.

## Técnicas
1. Normalización de tablas
2. Agregar índices faltantes
3. Optimizar relaciones JPA
4. Migraciones reversibles
5. Soft delete consistente

## Reglas
- Campos de auditoría en todas las tablas
- Índices en foreign keys
- Compatible MariaDB y H2
- Rollback siempre incluido
