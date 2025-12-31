package com.school.core.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.school.core.entity.AuditLog;
import com.school.core.repository.AuditLogRepository;

@Service
public class AuditService {

  private final AuditLogRepository auditLogRepository;

  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void saveAuditLog(@NonNull AuditLog auditLog) {
    auditLogRepository.save(auditLog);
  }

  public void logStudentDeletion(@NonNull Long studentId, @NonNull String currentUser) {
    AuditLog log = new AuditLog();
    log.setAction("STUDENT_DELETION");
    log.setEntityName("Student");
    log.setEntityId(studentId.toString());
    log.setPerformedBy(currentUser);
    log.setTimestamp(java.time.LocalDateTime.now());
    auditLogRepository.save(log);
  }

  public void logSectionDeletion(@NonNull Long sectionId, @NonNull String currentUser) {
    AuditLog log = new AuditLog();
    log.setAction("SECTION_DELETION");
    log.setEntityName("Section");
    log.setEntityId(sectionId.toString());
    log.setPerformedBy(currentUser);
    log.setTimestamp(java.time.LocalDateTime.now());
    auditLogRepository.save(log);
  }

  public void logCourseDeletion(@NonNull Long courseId, @NonNull String currentUser) {
    AuditLog log = new AuditLog();
    log.setAction("COURSE_DELETION");
    log.setEntityName("Course");
    log.setEntityId(courseId.toString());
    log.setPerformedBy(currentUser);
    log.setTimestamp(java.time.LocalDateTime.now());
    auditLogRepository.save(log);
  }

  public void logStaffDeletion(@NonNull Long staffId, @NonNull String currentUser) {
    AuditLog log = new AuditLog();
    log.setAction("STAFF_DELETION");
    log.setEntityName("Staff");
    log.setEntityId(staffId.toString());
    log.setPerformedBy(currentUser);
    log.setTimestamp(java.time.LocalDateTime.now());
    auditLogRepository.save(log);
  }

  public void logGenericAction(@NonNull String action, @NonNull String details, @NonNull String currentUser) {
    AuditLog log = new AuditLog();
    log.setAction(action);
    log.setEntityName("System");
    log.setEntityId("0");
    log.setPerformedBy(currentUser);
    log.setTimestamp(java.time.LocalDateTime.now());
    log.setDetails(details); // Assuming AuditLog has a details field, let's check
    auditLogRepository.save(log);
  }
}
