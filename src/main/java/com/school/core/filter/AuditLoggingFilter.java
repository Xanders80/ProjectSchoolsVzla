package com.school.core.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);

            int status = response.getStatus();
            String username = extractUsernameFromContext(); // Método corregido

            if (status >= 200 && status < 300) {
                log.info("REQ_ID={} | USER={} | METHOD={} | ENDPOINT={} | STATUS={} | IP={} | DURATION={}ms",
                        requestId, username, method, endpoint, status, remoteAddr,
                        System.currentTimeMillis() - startTime);
            } else {
                log.warn("REQ_ID={} | USER={} | METHOD={} | ENDPOINT={} | STATUS={} | IP={} | DURATION={}ms",
                        requestId, username, method, endpoint, status, remoteAddr,
                        System.currentTimeMillis() - startTime);
            }

        } catch (Exception e) {
            log.error("REQ_ID={} | ERROR={} | METHOD={} | ENDPOINT={} | IP={} | DURATION={}ms",
                    requestId, e.getMessage(), method, endpoint, remoteAddr,
                    System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    /**
     * Extracción segura del nombre de usuario desde el contexto de seguridad
     */
    private String extractUsernameFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return "ANONYMOUS";
        }

        Object principal = auth.getPrincipal();

        // Caso principal: UserDetails (como User de Spring Security)
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User) principal).getUsername();
        }
        // Caso: String directa
        else if (principal instanceof String) {
            return (String) principal;
        }
        // Caso: Custom UserDetails
        else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        // Caso fallback: usar getName() del Authentication
        else {
            String name = auth.getName();
            return (name != null && !name.equals("anonymousUser")) ? name : "ANONYMOUS";
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}