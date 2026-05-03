-- ============================================
-- MIGRATION: Performance indexes + Promotion FK fix
-- Date: 2026-05-01
-- ============================================

-- Performance indexes for frequent queries
CREATE INDEX IF NOT EXISTS idx_grades_student_deleted_date
ON grades(student_id, deleted, date);

CREATE INDEX IF NOT EXISTS idx_attendance_section_date
ON attendance(section_id, date);

CREATE INDEX IF NOT EXISTS idx_enrollments_section_student
ON enrollments(section_id, student_id);

CREATE INDEX IF NOT EXISTS idx_lab_reservations_room_time
ON lab_reservations(room_id, start_time, end_time);

-- Promotion table: convert String FKs to proper BIGINT FKs
ALTER TABLE promotion
ADD COLUMN IF NOT EXISTS student_id_bigint BIGINT;

ALTER TABLE promotion
ADD COLUMN IF NOT EXISTS previous_section_id_bigint BIGINT;

ALTER TABLE promotion
ADD COLUMN IF NOT EXISTS new_section_id_bigint BIGINT;

-- Add soft delete columns to lab_reservations
ALTER TABLE lab_reservations
ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE lab_reservations
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL;
