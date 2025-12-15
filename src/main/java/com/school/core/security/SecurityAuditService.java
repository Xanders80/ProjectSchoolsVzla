package com.school.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuditService {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        securityLogger.info("LOGIN_SUCCESS: user={}, timestamp={}",
                auth.getName(), java.time.LocalDateTime.now());
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        securityLogger.warn("LOGIN_FAILED: user={}, timestamp={}",
                username, java.time.LocalDateTime.now());
    }

    public void logSecurityEvent(String event, String username, String details) {
        securityLogger.info("SECURITY_EVENT: event={}, user={}, details={}, timestamp={}",
                event, username, details, java.time.LocalDateTime.now());
    }

    public void logSuspiciousActivity(String activity, String clientIP, String userAgent) {
        securityLogger.warn("SUSPICIOUS_ACTIVITY: activity={}, ip={}, userAgent={}, timestamp={}",
                activity, clientIP, userAgent, java.time.LocalDateTime.now());
    }
}