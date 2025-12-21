package com.school.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.core.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
