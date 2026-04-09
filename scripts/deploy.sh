#!/bin/bash
set -euo pipefail

# Deploy script for School Management System
# Usage: ./deploy.sh [dev|staging|prod]

ENVIRONMENT="${1:-dev}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=== Deploying SMS to $ENVIRONMENT ==="

# Validate environment
case "$ENVIRONMENT" in
  dev|staging|prod) ;;
  *) echo "Error: Invalid environment. Use dev, staging, or prod."; exit 1 ;;
esac

# Build
echo "Building application..."
cd "$PROJECT_DIR"
mvn clean package -P"$ENVIRONMENT" -DskipTests

# Run tests
echo "Running tests..."
mvn test -Ptest

# Deploy
if [ "$ENVIRONMENT" = "prod" ]; then
  echo "Deploying to production..."
  
  # Backup current version
  if [ -f /opt/sms/app.jar ]; then
    echo "Backing up current version..."
    cp /opt/sms/app.jar "/opt/sms/app.jar.backup.$(date +%Y%m%d_%H%M%S)"
  fi
  
  # Deploy new version
  cp target/*.jar /opt/sms/app.jar
  
  # Restart service
  echo "Restarting service..."
  systemctl restart school-management
  
  # Health check
  echo "Waiting for service to start..."
  sleep 15
  HEALTH=$(curl -sf http://localhost:8080/actuator/health || echo "FAILED")
  
  if [ "$HEALTH" = "FAILED" ]; then
    echo "ERROR: Health check failed!"
    echo "Rolling back..."
    LATEST_BACKUP=$(ls -t /opt/sms/app.jar.backup.* 2>/dev/null | head -1)
    if [ -n "$LATEST_BACKUP" ]; then
      cp "$LATEST_BACKUP" /opt/sms/app.jar
      systemctl restart school-management
      echo "Rollback complete."
    fi
    exit 1
  fi
  
  echo "Deployment successful!"
  curl -s http://localhost:8080/actuator/health | python3 -m json.tool
else
  echo "Deploying to $ENVIRONMENT..."
  echo "Run: docker-compose --profile $ENVIRONMENT up -d"
fi

echo "=== Deployment complete ==="
