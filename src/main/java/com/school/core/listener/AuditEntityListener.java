package com.school.core.listener;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.school.core.config.ApplicationContextProvider;
import com.school.core.entity.AuditLog;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class AuditEntityListener {

	private static final Logger log = LoggerFactory.getLogger(AuditEntityListener.class);

	@PostPersist
	public void postPersist(Object target) {
		performAudit(target, "INSERT");
	}

	@PostUpdate
	public void postUpdate(Object target) {
		performAudit(target, "UPDATE");
	}

	@PostRemove
	public void postRemove(Object target) {
		performAudit(target, "DELETE");
	}

	private void performAudit(Object target, String action) {
		try {
			String entityName = target.getClass().getSimpleName();
			String entityId = "N/A";

			try {
				java.lang.reflect.Method getIdMethod = target.getClass().getMethod("getId");
				Object idObj = getIdMethod.invoke(target);
				if (idObj != null) {
					entityId = idObj.toString();
				}
			} catch (NoSuchMethodException e) {
				log.debug("Entity {} has no getId() method", entityName);
			} catch (ReflectiveOperationException e) {
				log.warn("Could not extract ID from entity {}: {}", entityName, e.getMessage());
			}

			String username = "Anonymous";
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.isAuthenticated()) {
				username = auth.getName();
			}

			AuditLog auditLog = new AuditLog(entityName, entityId, action, username, LocalDateTime.now());

			com.school.core.service.AuditService auditService = ApplicationContextProvider
					.getBean(com.school.core.service.AuditService.class);
			auditService.saveAuditLog(auditLog);
		} catch (Exception e) {
			log.error("Audit failed for {} action on {}: {}", action, target.getClass().getSimpleName(), e.getMessage(), e);
		}
	}
}
