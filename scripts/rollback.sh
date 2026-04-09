#!/bin/bash
set -euo pipefail

# Rollback script for School Management System
# Usage: ./rollback.sh [backup_file|latest]

BACKUP_TARGET="${1:-latest}"
APP_DIR="/opt/sms"
SERVICE_NAME="school-management"

echo "=== Rollback initiated ==="

# Stop service
echo "Stopping service..."
systemctl stop "$SERVICE_NAME"

# Find backup to restore
if [ "$BACKUP_TARGET" = "latest" ]; then
  BACKUP_FILE=$(ls -t "$APP_DIR"/app.jar.backup.* 2>/dev/null | head -1)
  if [ -z "$BACKUP_FILE" ]; then
    echo "ERROR: No backup files found!"
    echo "Looking for app.jar.backup.* in $APP_DIR"
    exit 1
  fi
  echo "Using latest backup: $BACKUP_FILE"
else
  BACKUP_FILE="$BACKUP_TARGET"
  if [ ! -f "$BACKUP_FILE" ]; then
    echo "ERROR: Backup file not found: $BACKUP_FILE"
    exit 1
  fi
fi

# Backup current version before rollback
CURRENT_BACKUP="$APP_DIR/app.jar.pre-rollback.$(date +%Y%m%d_%H%M%S)"
if [ -f "$APP_DIR/app.jar" ]; then
  echo "Saving current version as: $CURRENT_BACKUP"
  cp "$APP_DIR/app.jar" "$CURRENT_BACKUP"
fi

# Restore backup
echo "Restoring from backup..."
cp "$BACKUP_FILE" "$APP_DIR/app.jar"

# Start service
echo "Starting service..."
systemctl start "$SERVICE_NAME"

# Health check
echo "Waiting for service to start..."
sleep 15
HEALTH=$(curl -sf http://localhost:8080/actuator/health || echo "FAILED")

if [ "$HEALTH" = "FAILED" ]; then
  echo "ERROR: Health check failed after rollback!"
  echo "Attempting to restore pre-rollback version..."
  if [ -f "$CURRENT_BACKUP" ]; then
    cp "$CURRENT_BACKUP" "$APP_DIR/app.jar"
    systemctl restart "$SERVICE_NAME"
    echo "Pre-rollback version restored."
  fi
  exit 1
fi

echo "=== Rollback successful ==="
echo "Service health: $(curl -s http://localhost:8080/actuator/health)"
