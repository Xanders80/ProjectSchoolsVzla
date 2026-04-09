# Error Handler - Rollback Procedure Prompt

## Procedimiento de Rollback para SMS

### Rollback de Código
```bash
# Verificar último commit estable
git log --oneline -10

# Revertir último commit
git revert HEAD

# O volver a un commit específico
git reset --hard <commit-hash>
```

### Rollback de Base de Datos
```bash
# Restaurar desde backup
mysql -u root -p dbSchollAdm < /opt/backups/sms/latest_backup.sql

# O ejecutar migration de rollback
# (cada migration debe tener su rollback SQL)
```

### Rollback de Deployment
```bash
# Detener servicio
systemctl stop school-management

# Restaurar JAR anterior
cp /opt/sms/backup/school-management-*.jar /opt/sms/app.jar

# Reiniciar servicio
systemctl start school-management
```

### Checklist de Rollback
- [ ] Identificar versión estable
- [ ] Notificar al equipo
- [ ] Ejecutar rollback de código
- [ ] Ejecutar rollback de BD si es necesario
- [ ] Verificar que el sistema funciona
- [ ] Documentar incidente
- [ ] Planificar fix correcto
