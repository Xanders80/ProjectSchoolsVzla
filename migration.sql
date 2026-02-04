-- ============================================
-- SCRIPT DE MIGRACIÓN - Sistema de Gestión de Activos v2.0
-- Base de Datos: dbSchollAdm
-- Fecha: 2026-02-02
-- ============================================

-- Crear tablas de respaldo (seguridad)
CREATE TABLE IF NOT EXISTS assets_backup AS SELECT * FROM assets;
CREATE TABLE IF NOT EXISTS maintenance_requests_backup AS SELECT * FROM maintenance_requests;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- PARTE 1: MODIFICACIONES A TABLA ASSETS
-- ============================================

-- Agregar nuevas columnas a assets
ALTER TABLE assets
    ADD COLUMN IF NOT EXISTS category VARCHAR(50) AFTER name,
    ADD COLUMN IF NOT EXISTS status VARCHAR(30) DEFAULT 'ACTIVE' AFTER serial_number,
    ADD COLUMN IF NOT EXISTS `condition` VARCHAR(20) DEFAULT 'GOOD' AFTER status,
    ADD COLUMN IF NOT EXISTS purchase_value DECIMAL(10,2) AFTER purchase_date,
    ADD COLUMN IF NOT EXISTS depreciation_rate DECIMAL(5,4) DEFAULT 0.15 AFTER purchase_value,
    ADD COLUMN IF NOT EXISTS manufacturer VARCHAR(100) AFTER depreciation_rate,
    ADD COLUMN IF NOT EXISTS model VARCHAR(100) AFTER manufacturer,
    ADD COLUMN IF NOT EXISTS warranty_months INT DEFAULT 12 AFTER model;

-- Migrar datos de type a category (solo si category es NULL)
UPDATE assets
SET category = CASE
    WHEN LOWER(type) LIKE '%mobiliario%' OR LOWER(type) LIKE '%furniture%' THEN 'FURNITURE'
    WHEN LOWER(type) LIKE '%electr%' OR LOWER(type) LIKE '%electronic%' THEN 'ELECTRONICS'
    WHEN LOWER(type) LIKE '%laboratorio%' OR LOWER(type) LIKE '%lab%' THEN 'LABORATORY_EQUIPMENT'
    WHEN LOWER(type) LIKE '%deporte%' OR LOWER(type) LIKE '%sport%' THEN 'SPORTS_EQUIPMENT'
    WHEN LOWER(type) LIKE '%oficina%' OR LOWER(type) LIKE '%office%' THEN 'OFFICE_SUPPLIES'
    WHEN LOWER(type) LIKE '%vehic%' OR LOWER(type) LIKE '%vehicle%' THEN 'VEHICLE'
    WHEN LOWER(type) LIKE '%edifici%' OR LOWER(type) LIKE '%building%' THEN 'BUILDING_INFRASTRUCTURE'
    ELSE 'OTHER'
END
WHERE category IS NULL;

-- Hacer columnas obligatorias
ALTER TABLE assets
    MODIFY COLUMN category VARCHAR(50) NOT NULL,
    MODIFY COLUMN status VARCHAR(30) NOT NULL,
    MODIFY COLUMN `condition` VARCHAR(20) NOT NULL;

-- Agregar índice único a serial_number si no existe
ALTER TABLE assets
    ADD UNIQUE KEY IF NOT EXISTS uk_asset_serial (serial_number);

-- Crear índices si no existen
CREATE INDEX IF NOT EXISTS idx_asset_category_status ON assets(category, status);

-- ============================================
-- PARTE 2: MODIFICACIONES A TABLA MAINTENANCE_REQUESTS
-- ============================================

-- Agregar nuevas columnas a maintenance_requests
ALTER TABLE maintenance_requests
    ADD COLUMN IF NOT EXISTS asset_id BIGINT AFTER id,
    ADD COLUMN IF NOT EXISTS assigned_to_user_id BIGINT AFTER requested_by_user_id,
    ADD COLUMN IF NOT EXISTS priority VARCHAR(20) DEFAULT 'MEDIUM' AFTER description,
    ADD COLUMN IF NOT EXISTS type VARCHAR(30) AFTER priority,
    ADD COLUMN IF NOT EXISTS scheduled_date DATE AFTER request_date,
    ADD COLUMN IF NOT EXISTS due_date DATE AFTER scheduled_date,
    ADD COLUMN IF NOT EXISTS resolution TEXT AFTER completion_date,
    ADD COLUMN IF NOT EXISTS estimated_cost DECIMAL(10,2) AFTER resolution,
    ADD COLUMN IF NOT EXISTS actual_cost DECIMAL(10,2) AFTER estimated_cost;

-- Normalizar valores de status existentes
UPDATE maintenance_requests
SET status = CASE
    WHEN UPPER(status) IN ('PENDING', 'PENDIENTE', 'ESPERA') THEN 'PENDING'
    WHEN UPPER(status) IN ('IN_PROGRESS', 'EN PROGRESO', 'PROCESO', 'IN PROGRESS') THEN 'IN_PROGRESS'
    WHEN UPPER(status) IN ('COMPLETED', 'COMPLETADO', 'FINALIZADO', 'DONE') THEN 'COMPLETED'
    WHEN UPPER(status) IN ('CANCELLED', 'CANCELADO') THEN 'CANCELLED'
    ELSE 'PENDING'
END;

-- Establecer tipo por defecto basado en descripción (heurística simple)
UPDATE maintenance_requests
SET type = CASE
    WHEN description LIKE '%emergencia%' OR description LIKE '%emergency%' THEN 'EMERGENCY'
    WHEN description LIKE '%preventivo%' OR description LIKE '%preventive%' THEN 'PREVENTIVE'
    WHEN description LIKE '%inspecci%' OR description LIKE '%inspection%' THEN 'INSPECTION'
    ELSE 'CORRECTIVE'
END
WHERE type IS NULL;

-- Calcular due_date basado en fecha de solicitud y prioridad
UPDATE maintenance_requests
SET due_date = CASE
    WHEN priority = 'CRITICAL' THEN DATE_ADD(DATE(request_date), INTERVAL 1 DAY)
    WHEN priority = 'HIGH' THEN DATE_ADD(DATE(request_date), INTERVAL 7 DAY)
    WHEN priority = 'LOW' THEN DATE_ADD(DATE(request_date), INTERVAL 30 DAY)
    ELSE DATE_ADD(DATE(request_date), INTERVAL 15 DAY)
END
WHERE due_date IS NULL;

-- Hacer columnas obligatorias
ALTER TABLE maintenance_requests
    MODIFY COLUMN status VARCHAR(30) NOT NULL,
    MODIFY COLUMN priority VARCHAR(20) NOT NULL,
    MODIFY COLUMN type VARCHAR(30) NOT NULL;

-- Hacer room_id opcional (ahora puede ser NULL si hay asset_id)
ALTER TABLE maintenance_requests
    MODIFY COLUMN room_id BIGINT NULL;

-- Agregar claves foráneas si no existen
-- Nota: MariaDB requiere eliminar constraint antes de agregarlo si existe
ALTER TABLE maintenance_requests
    ADD CONSTRAINT fk_maintenance_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE SET NULL;

ALTER TABLE maintenance_requests
    ADD CONSTRAINT fk_maintenance_assigned_to
        FOREIGN KEY (assigned_to_user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Crear índices si no existen
CREATE INDEX IF NOT EXISTS idx_maintenance_status ON maintenance_requests(status);
CREATE INDEX IF NOT EXISTS idx_maintenance_priority ON maintenance_requests(priority);
CREATE INDEX IF NOT EXISTS idx_maintenance_due_date ON maintenance_requests(due_date);
CREATE INDEX IF NOT EXISTS idx_maintenance_asset ON maintenance_requests(asset_id);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- PARTE 3: VALIDACIONES
-- ============================================

-- Verificar que todos los assets tienen category válida
SELECT COUNT(*) AS assets_sin_category_valida
FROM assets
WHERE category NOT IN ('FURNITURE', 'ELECTRONICS', 'LABORATORY_EQUIPMENT',
                       'SPORTS_EQUIPMENT', 'OFFICE_SUPPLIES', 'VEHICLE',
                       'BUILDING_INFRASTRUCTURE', 'OTHER');

-- Verificar que todas las solicitudes tienen status válido
SELECT COUNT(*) AS requests_status_invalido
FROM maintenance_requests
WHERE status NOT IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');

-- Verificar integridad: maintenance_requests deben tener asset O room
SELECT COUNT(*) AS requests_sin_asset_ni_room
FROM maintenance_requests
WHERE asset_id IS NULL AND room_id IS NULL;

-- ============================================
-- RESUMEN DE CAMBIOS
-- ============================================

SELECT
    'Assets' AS tabla,
    COUNT(*) AS total_registros,
    SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) AS activos,
    SUM(CASE WHEN status = 'IN_MAINTENANCE' THEN 1 ELSE 0 END) AS en_mantenimiento
FROM assets
UNION ALL
SELECT
    'Maintenance Requests' AS tabla,
    COUNT(*) AS total_registros,
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pendientes,
    SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS en_progreso
FROM maintenance_requests;

-- Migración completada exitosamente
SELECT 'MIGRACIÓN COMPLETADA EXITOSAMENTE' AS resultado;
