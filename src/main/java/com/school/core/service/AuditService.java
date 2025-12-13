package com.school.core.service;

import com.school.core.entity.AuditLog;
import com.school.core.repository.AuditLogRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

  private final AuditLogRepository auditLogRepository;

  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void saveAuditLog(@NonNull AuditLog auditLog) {
    auditLogRepository.save(auditLog);
  }
}
