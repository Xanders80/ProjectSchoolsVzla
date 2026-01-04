package com.school.core.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DeleteRateLimitFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(DeleteRateLimitFilter.class);
    private static final int MAX_DELETE_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60000; // 1 minuto
    
    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if (isDeleteEndpoint(httpRequest)) {
            String clientIp = getClientIp(httpRequest);
            
            if (isRateLimited(clientIp)) {
                logger.warn("Rate limit exceeded for delete operation from IP: {}", clientIp);
                httpResponse.setStatus(429);
                httpResponse.getWriter().write("{\"error\":\"Too many delete attempts\"}");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }

    private boolean isDeleteEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        return ("DELETE".equals(method) || "POST".equals(method)) && 
               (uri.contains("/delete/") || uri.endsWith("/delete"));
    }

    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private boolean isRateLimited(String clientIp) {
        long now = System.currentTimeMillis();
        AttemptRecord record = attempts.compute(clientIp, (key, existing) -> {
            if (existing == null || now - existing.windowStart > WINDOW_MS) {
                return new AttemptRecord(now, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });
        
        return record.count.get() > MAX_DELETE_ATTEMPTS;
    }

    private static class AttemptRecord {
        final long windowStart;
        final AtomicInteger count;
        
        AttemptRecord(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}