package com.school.core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuditLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String endpoint = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String remoteAddr = getClientIpAddress(httpRequest);
        
        long startTime = System.currentTimeMillis();
        
        try {
            chain.doFilter(request, response);
            
            int status = httpResponse.getStatus();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "ANONYMOUS";
            
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
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}