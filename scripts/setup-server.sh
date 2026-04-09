#!/bin/bash
set -euo pipefail

# Server setup script for School Management System
# Run as root on a fresh Ubuntu 22.04/24.04 server

echo "=== Setting up SMS server ==="

# Update system
apt update && apt upgrade -y

# Install dependencies
apt install -y \
  openjdk-21-jre-headless \
  mariadb-server \
  nginx \
  certbot \
  python3-certbot-nginx \
  wget \
  curl \
  unzip \
  git

# Create SMS user
if ! id -u sms &>/dev/null; then
  useradd -r -s /bin/false -d /opt/sms sms
  echo "Created sms user"
fi

# Create directories
mkdir -p /opt/sms
mkdir -p /opt/backups/sms/{database,application,logs}
mkdir -p /var/log/sms
chown -R sms:sms /opt/sms /var/log/sms

# Setup MariaDB
echo "Configuring MariaDB..."
mysql -e "CREATE DATABASE IF NOT EXISTS dbSchollAdm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "CREATE USER IF NOT EXISTS 'sms_user'@'localhost' IDENTIFIED BY '${SMS_DB_PASSWORD:-ChangeMe123!}';"
mysql -e "GRANT ALL PRIVILEGES ON dbSchollAdm.* TO 'sms_user'@'localhost';"
mysql -e "FLUSH PRIVILEGES;"

# Install service
cp deploy/school-management.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable school-management

# Setup nginx
cp deploy/nginx.conf /etc/nginx/sites-available/sms
ln -sf /etc/nginx/sites-available/sms /etc/nginx/sites-enabled/sms
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl reload nginx

# Setup logrotate
cat > /etc/logrotate.d/school-management << 'EOF'
/var/log/sms/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0640 sms sms
    sharedscripts
    postrotate
        systemctl reload school-management > /dev/null 2>&1 || true
    endscript
}
EOF

# Setup backup cron
if ! crontab -l -u root 2>/dev/null | grep -q "backup.sh"; then
  (crontab -l -u root 2>/dev/null; echo "0 2 * * * /opt/sms/scripts/backup.sh full") | crontab -u root -
  echo "Backup cron added"
fi

echo "=== Server setup complete ==="
echo ""
echo "Next steps:"
echo "1. Copy app.jar to /opt/sms/app.jar"
echo "2. Configure SSL: certbot --nginx -d your-domain.com"
echo "3. Update database password in application-prod.properties"
echo "4. Start service: systemctl start school-management"
