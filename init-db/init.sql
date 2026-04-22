-- Database initialization script
-- Run as non-root user with limited privileges

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS `app_database` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create dedicated user with minimal privileges
CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY 'CHANGE_ME_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE ON `app_database`.* TO 'app_user'@'%';

-- Use secure password (should be replaced via secrets)
ALTER USER 'app_user'@'%' IDENTIFIED WITH mysql_native_password BY 'CHANGE_ME_PASSWORD';

-- Application tables (example schema)
USE `app_database`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email),
  INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `sessions` (
  `id` VARCHAR(128) PRIMARY KEY,
  `user_id` INT,
  `ip_address` VARCHAR(45),
  `user_agent` VARCHAR(255),
  `last_activity` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `expires_at` DATETIME,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initial admin user (CHANGE THIS IN PRODUCTION)
INSERT INTO `users` (`username`, `email`, `password_hash`) VALUES
  ('admin', 'admin@example.com', '$2y$10$ExampleHashForDevOnlyChangeInProduction'),
  ('system', 'system@example.com', '$2y$10$ExampleHashForDevOnlyChangeInProduction');

-- Security: Remove test data
DELETE FROM `users` WHERE `username` IN ('test', 'root', 'demo');

-- Verify setup
SELECT 'Database initialized successfully' AS status;