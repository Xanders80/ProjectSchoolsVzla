package com.school.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Validated
public abstract class BaseDeleteController {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected void logDeleteAttempt(String entity, String id, HttpServletRequest request, boolean success, String error) {
        String clientIp = getClientIp(request);
        String user = getCurrentUser();
        
        if (success) {
            logger.info("{} {} deleted successfully by user: {} from IP: {}", entity, id, user, clientIp);
        } else {
            logger.warn("Failed to delete {} {}: {} - User: {} IP: {}", entity, id, error, user, clientIp);
        }
    }
    
    protected void handleDeleteResult(boolean success, String successMsg, String errorMsg, 
                                    RedirectAttributes redirectAttributes) {
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", successMsg);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private String getCurrentUser() {
        try {
            return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "anonymous";
        }
    }
}