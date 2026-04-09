# Safety Guard - Dangerous Operations Prompt

## Detección de Operaciones Peligrosas para SMS

### Operaciones Bloqueadas
- `rm -rf` en directorios del proyecto
- `DROP TABLE` sin confirmación
- `DELETE FROM` sin WHERE
- `UPDATE` sin WHERE en producción
- `chmod 777` en archivos sensibles
- Modificación de `.gitignore` para excluir archivos de seguridad
- Commit de archivos `.env` o credenciales

### Operaciones que Requieren Confirmación
- Migraciones de base de datos destructivas
- Eliminación de entidades o tablas
- Cambios en SecurityConfig
- Modificación de roles y permisos
- Cambios en configuración de producción

### Lista de Comandos Prohibidos
```yaml
forbidden_commands:
  - "rm -rf"
  - "DROP DATABASE"
  - "DROP TABLE"
  - "TRUNCATE TABLE"
  - "chmod 777"
  - "curl.*password"
  - "wget.*secret"
  - "git push --force"
  - "git reset --hard origin"
```

### Patrón de Validación
1. Analizar comando antes de ejecutar
2. Verificar contra lista de comandos prohibidos
3. Si es peligroso pero necesario, solicitar confirmación explícita
4. Registrar en audit log
