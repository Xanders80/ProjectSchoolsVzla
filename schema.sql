-- SQL Schema for School Management System (MariaDB/MySQL)
-- Generated from JPA Entities

SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `enabled` BIT(1) NOT NULL DEFAULT b'1',
  `created_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `previous_username` VARCHAR(50) DEFAULT NULL,
  `username_changed_at` DATETIME(6) DEFAULT NULL,
  `password` VARCHAR(255) NOT NULL,
  `password_changed_at` DATETIME(6) DEFAULT NULL,
  `role` VARCHAR(20) NOT NULL,
  `enabled` BIT(1) NOT NULL DEFAULT b'1',
  `created_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  KEY `idx_user_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `audit_logs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE `audit_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `entity_name` VARCHAR(255) DEFAULT NULL,
  `entity_id` VARCHAR(255) DEFAULT NULL,
  `action` VARCHAR(255) DEFAULT NULL,
  `performed_by` VARCHAR(255) DEFAULT NULL,
  `timestamp` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `buildings`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `buildings`;
CREATE TABLE `buildings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `previous_name` VARCHAR(100) DEFAULT NULL,
  `name_changed_at` DATETIME(6) DEFAULT NULL,
  `address` VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_building_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `rooms`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rooms`;
CREATE TABLE `rooms` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `room_number` VARCHAR(20) NOT NULL,
  `previous_room_number` VARCHAR(20) DEFAULT NULL,
  `room_number_changed_at` DATETIME(6) DEFAULT NULL,
  `capacity` INT DEFAULT NULL,
  `type` VARCHAR(100) DEFAULT NULL,
  `building_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_room_number` (`room_number`),
  KEY `idx_room_building` (`building_id`),
  KEY `idx_room_type` (`type`),
  CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `staff`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `dni` VARCHAR(20) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(15) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `birth_date` DATE DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `job_title` VARCHAR(20) NOT NULL,
  `salary` DECIMAL(10,2) DEFAULT NULL,
  `hire_date` DATE NOT NULL,
  `department` VARCHAR(100) NOT NULL,
  `previous_department` VARCHAR(100) DEFAULT NULL,
  `department_changed_at` DATETIME(6) DEFAULT NULL,
  `specialization` VARCHAR(200) DEFAULT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT b'0',
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `deleted_by` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_staff_dni` (`dni`),
  UNIQUE KEY `uk_staff_user` (`user_id`),
  KEY `idx_staff_job_title` (`job_title`),
  KEY `idx_staff_department` (`department`),
  KEY `idx_staff_hire_date` (`hire_date`),
  CONSTRAINT `fk_staff_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `staff_contracts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `staff_contracts`;
CREATE TABLE `staff_contracts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `staff_id` BIGINT NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE DEFAULT NULL,
  `position` VARCHAR(255) NOT NULL,
  `salary` DECIMAL(10,2) NOT NULL,
  `contract_type` VARCHAR(20) DEFAULT NULL,
  `is_active` BIT(1) DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_staff` (`staff_id`),
  CONSTRAINT `fk_contract_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `staff_payroll`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `staff_payroll`;
CREATE TABLE `staff_payroll` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `staff_id` BIGINT NOT NULL,
  `period` VARCHAR(7) NOT NULL,
  `base_salary` DECIMAL(10,2) DEFAULT NULL,
  `bonuses` DECIMAL(10,2) DEFAULT '0.00',
  `deductions` DECIMAL(10,2) DEFAULT '0.00',
  `net_salary` DECIMAL(10,2) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT NULL,
  `payment_date` DATE DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payroll_staff` (`staff_id`),
  CONSTRAINT `fk_payroll_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `academic_periods`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `academic_periods`;
CREATE TABLE `academic_periods` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(10) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_period_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `courses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(10) NOT NULL,
  `previous_code` VARCHAR(10) DEFAULT NULL,
  `code_changed_at` DATETIME(6) DEFAULT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) DEFAULT NULL,
  `credits` INT NOT NULL,
  `grade_level` INT NOT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT b'0',
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `deleted_by` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`code`),
  KEY `idx_course_grade_level` (`grade_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `students`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `dni` VARCHAR(20) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(15) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `birth_date` DATE DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `registration_number` VARCHAR(20) NOT NULL,
  `previous_registration_number` VARCHAR(20) DEFAULT NULL,
  `registration_changed_at` DATETIME(6) DEFAULT NULL,
  `enrollment_date` DATE NOT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT b'0',
  `deleted_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_dni` (`dni`),
  UNIQUE KEY `uk_student_registration` (`registration_number`),
  UNIQUE KEY `uk_student_user` (`user_id`),
  KEY `idx_student_enrollment_date` (`enrollment_date`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `sections`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sections`;
CREATE TABLE `sections` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `period_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `teacher_id` BIGINT DEFAULT NULL,
  `room_id` BIGINT DEFAULT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT b'0',
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `deleted_by` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_section_period` (`period_id`),
  KEY `idx_section_course` (`course_id`),
  CONSTRAINT `fk_section_period` FOREIGN KEY (`period_id`) REFERENCES `academic_periods` (`id`),
  CONSTRAINT `fk_section_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_section_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `staff` (`id`),
  CONSTRAINT `fk_section_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `enrollments`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `enrollments`;
CREATE TABLE `enrollments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `section_id` BIGINT NOT NULL,
  `grade` DOUBLE DEFAULT NULL,
  `enrollment_date` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_enrollment_student` (`student_id`),
  KEY `idx_enrollment_section` (`section_id`),
  CONSTRAINT `fk_enrollment_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `fk_enrollment_section` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `grades`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `grades`;
CREATE TABLE `grades` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `period_id` BIGINT NOT NULL,
  `score` DOUBLE NOT NULL,
  `evaluation_type` VARCHAR(255) NOT NULL,
  `date` DATE NOT NULL,
  `comments` TEXT,
  PRIMARY KEY (`id`),
  KEY `idx_grade_student` (`student_id`),
  KEY `idx_grade_course` (`course_id`),
  KEY `idx_grade_period` (`period_id`),
  CONSTRAINT `fk_grade_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `fk_grade_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_grade_period` FOREIGN KEY (`period_id`) REFERENCES `academic_periods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `schedule_entries`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `schedule_entries`;
CREATE TABLE `schedule_entries` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `day_of_week` VARCHAR(255) DEFAULT NULL,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `section_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_schedule_section` (`section_id`),
  CONSTRAINT `fk_schedule_section` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `medical_records`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical_records`;
CREATE TABLE `medical_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `blood_type` VARCHAR(10) DEFAULT NULL,
  `allergies` TEXT,
  `medications` TEXT,
  `conditions` TEXT,
  `emergency_contact_name` VARCHAR(100) DEFAULT NULL,
  `emergency_contact_phone` VARCHAR(20) DEFAULT NULL,
  `last_updated` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_medical_student` (`student_id`),
  CONSTRAINT `fk_medical_record_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `books`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `isbn` VARCHAR(255) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `author` VARCHAR(255) NOT NULL,
  `category` VARCHAR(255) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_isbn` (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `student_fees`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `student_fees`;
CREATE TABLE `student_fees` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `due_date` DATE NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `payment_date` DATE DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fee_student` (`student_id`),
  CONSTRAINT `fk_fee_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `payments`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `fee_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_date` DATE NOT NULL,
  `method` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_fee` (`fee_id`),
  CONSTRAINT `fk_payment_fee` FOREIGN KEY (`fee_id`) REFERENCES `student_fees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `parents`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `parents`;
CREATE TABLE `parents` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `dni` VARCHAR(20) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(15) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `birth_date` DATE DEFAULT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `relationship` VARCHAR(50) NOT NULL,
  `previous_relationship` VARCHAR(50) DEFAULT NULL,
  `relationship_changed_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_parent_dni` (`dni`),
  UNIQUE KEY `uk_parent_user` (`user_id`),
  CONSTRAINT `fk_parent_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `parent_student` (Join Table)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `parent_student`;
CREATE TABLE `parent_student` (
  `parent_id` BIGINT NOT NULL,
  `student_id` BIGINT NOT NULL,
  PRIMARY KEY (`parent_id`, `student_id`),
  KEY `idx_parent_student_parent` (`parent_id`),
  KEY `idx_parent_student_student` (`student_id`),
  CONSTRAINT `fk_parent_student_parent` FOREIGN KEY (`parent_id`) REFERENCES `parents` (`id`),
  CONSTRAINT `fk_parent_student_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `messages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sender_id` BIGINT NOT NULL,
  `receiver_id` BIGINT NOT NULL,
  `subject` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `sent_at` DATETIME(6) NOT NULL,
  `is_read` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_message_sender` (`sender_id`),
  KEY `idx_message_receiver` (`receiver_id`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
