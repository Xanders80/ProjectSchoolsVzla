package com.school.core.listener;

import com.school.core.config.ApplicationContextProvider;
import com.school.core.entity.AuditLog;

import jakarta.persistence.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class AuditEntityListener {

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

            // Try to get ID via reflection or common interface if available.
            // For now, we'll try simplistic reflection or just store N/A for prePersist (ID
            // might be null)
            // But for Update/Delete ID should be there.
            try {
                java.lang.reflect.Method getIdMethod = target.getClass().getMethod("getId");
                Object idObj = getIdMethod.invoke(target);
                if (idObj != null) {
                    entityId = idObj.toString();
                }
            } catch (Exception e) {
                // Ignore if no getId
            }

            String username = "Anonymous";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                username = auth.getName();
            }

            AuditLog log = new AuditLog(entityName, entityId, action, username, LocalDateTime.now());

            com.school.core.service.AuditService auditService = ApplicationContextProvider
                    .getBean(com.school.core.service.AuditService.class);
            auditService.saveAuditLog(log);
        } catch (Exception e) {
            e.printStackTrace(); // Log error but don't fail operation
        }
    }
}
