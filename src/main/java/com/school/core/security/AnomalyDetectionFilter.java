package com.school.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnomalyDetectionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AnomalyDetectionFilter.class);
    private final ConcurrentHashMap<String, Integer> requestCount = new ConcurrentHashMap<>();
    private static final int THRESHOLD = 10;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        
        if (isAnonymousRequest(request) && isProtectedEndpoint(endpoint)) {
            String key = clientIp + ":" + endpoint;
            int count = requestCount.merge(key, 1, Integer::sum);
            
            log.warn("ANONYMOUS_ACCESS_ATTEMPT | IP={} | ENDPOINT={} | COUNT={}", 
                    clientIp, endpoint, count);
            
            if (count > THRESHOLD) {
                response.setStatus(429);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isAnonymousRequest(HttpServletRequest request) {
        return request.getUserPrincipal() == null;
    }
    
    private boolean isProtectedEndpoint(String endpoint) {
        return endpoint.startsWith("/students") || endpoint.startsWith("/sections") || 
               endpoint.startsWith("/admin");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}