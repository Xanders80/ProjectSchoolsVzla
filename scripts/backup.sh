#!/bin/bash
set -euo pipefail

# Backup script for School Management System
# Usage: ./backup.sh [full|db|app]
# Cron: 0 2 * * * /opt/sms/scripts/backup.sh full

BACKUP_TYPE="${1:-full}"
BACKUP_DIR="/opt/backups/sms"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Database credentials
DB_NAME="${DB_NAME:-dbSchollAdm}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-}"

echo "=== Starting $BACKUP_TYPE backup ==="
echo "Date: $DATE"
echo "Backup directory: $BACKUP_DIR"

# Create backup directory
mkdir -p "$BACKUP_DIR"/{database,application,logs}

backup_database() {
  echo "Backing up database..."
  BACKUP_FILE="$BACKUP_DIR/database/sms_db_$DATE.sql.gz"
  
  if [ -n "$DB_PASS" ]; then
    mysqldump -u "$DB_USER" -p"$DB_PASS" \
      --single-transaction \
      --routines \
      --triggers \
      --events \
      "$DB_NAME" | gzip > "$BACKUP_FILE"
  else
    mysqldump -u "$DB_USER" \
      --single-transaction \
      --routines \
      --triggers \
      --events \
      "$DB_NAME" | gzip > "$BACKUP_FILE"
  fi
  
  if [ -f "$BACKUP_FILE" ]; then
    SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    echo "Database backup complete: $BACKUP_FILE ($SIZE)"
  else
    echo "ERROR: Database backup failed!"
    return 1
  fi
}

backup_application() {
  echo "Backing up application..."
  APP_BACKUP="$BACKUP_DIR/application/sms_app_$DATE.tar.gz"
  
  if [ -f /opt/sms/app.jar ]; then
    tar czf "$APP_BACKUP" -C /opt/sms app.jar
    SIZE=$(du -h "$APP_BACKUP" | cut -f1)
    echo "Application backup complete: $APP_BACKUP ($SIZE)"
  else
    echo "WARNING: No application JAR found at /opt/sms/app.jar"
  fi
}

backup_logs() {
  echo "Backing up logs..."
  LOG_BACKUP="$BACKUP_DIR/logs/sms_logs_$DATE.tar.gz"
  
  if [ -d /var/log/sms ]; then
    tar czf "$LOG_BACKUP" -C /var/log sms
    SIZE=$(du -h "$LOG_BACKUP" | cut -f1)
    echo "Logs backup complete: $LOG_BACKUP ($SIZE)"
  else
    echo "WARNING: No logs directory found at /var/log/sms"
  fi
}

cleanup_old_backups() {
  echo "Cleaning up backups older than $RETENTION_DAYS days..."
  find "$BACKUP_DIR" -name "*.gz" -mtime +$RETENTION_DAYS -delete
  find "$BACKUP_DIR" -name "*.sql" -mtime +$RETENTION_DAYS -delete
  echo "Cleanup complete."
}

# Execute backup based on type
case "$BACKUP_TYPE" in
  full)
    backup_database
    backup_application
    backup_logs
    cleanup_old_backups
    ;;
  db)
    backup_database
    ;;
  app)
    backup_application
    ;;
  *)
    echo "Error: Invalid backup type. Use full, db, or app."
    exit 1
    ;;
esac

echo "=== Backup complete ==="
