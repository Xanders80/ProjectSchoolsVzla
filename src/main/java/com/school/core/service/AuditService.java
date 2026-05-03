package com.school.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.school.core.entity.AuditLog;
import com.school.core.repository.AuditLogRepository;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AuditService {

	private static final Logger log = LoggerFactory.getLogger(AuditService.class);

	private final AuditLogRepository auditLogRepository;

	public AuditService(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	public void saveAuditLog(@NonNull AuditLog auditLog) {
		try {
			auditLogRepository.save(auditLog);
		} catch (Exception e) {
			log.error("Failed to save audit log: action={}, entity={}", auditLog.getAction(), auditLog.getEntityName(), e);
		}
	}

	public void logStudentDeletion(@NonNull Long studentId, @NonNull String currentUser) {
		AuditLog logEntry = new AuditLog();
		logEntry.setAction("STUDENT_DELETION");
		logEntry.setEntityName("Student");
		logEntry.setEntityId(studentId.toString());
		logEntry.setPerformedBy(currentUser);
		logEntry.setTimestamp(java.time.LocalDateTime.now());
		saveAuditLog(logEntry);
	}

	public void logSectionDeletion(@NonNull Long sectionId, @NonNull String currentUser) {
		AuditLog logEntry = new AuditLog();
		logEntry.setAction("SECTION_DELETION");
		logEntry.setEntityName("Section");
		logEntry.setEntityId(sectionId.toString());
		logEntry.setPerformedBy(currentUser);
		logEntry.setTimestamp(java.time.LocalDateTime.now());
		saveAuditLog(logEntry);
	}

	public void logCourseDeletion(@NonNull Long courseId, @NonNull String currentUser) {
		AuditLog logEntry = new AuditLog();
		logEntry.setAction("COURSE_DELETION");
		logEntry.setEntityName("Course");
		logEntry.setEntityId(courseId.toString());
		logEntry.setPerformedBy(currentUser);
		logEntry.setTimestamp(java.time.LocalDateTime.now());
		saveAuditLog(logEntry);
	}

	public void logStaffDeletion(@NonNull Long staffId, @NonNull String currentUser) {
		AuditLog logEntry = new AuditLog();
		logEntry.setAction("STAFF_DELETION");
		logEntry.setEntityName("Staff");
		logEntry.setEntityId(staffId.toString());
		logEntry.setPerformedBy(currentUser);
		logEntry.setTimestamp(java.time.LocalDateTime.now());
		saveAuditLog(logEntry);
	}

	public void logGenericAction(@NonNull String action, @NonNull String details, @NonNull String currentUser) {
		AuditLog logEntry = new AuditLog();
		logEntry.setAction(action);
		logEntry.setEntityName("System");
		logEntry.setEntityId("0");
		logEntry.setPerformedBy(currentUser);
		logEntry.setTimestamp(java.time.LocalDateTime.now());
		logEntry.setDetails(details);
		saveAuditLog(logEntry);
	}
}
