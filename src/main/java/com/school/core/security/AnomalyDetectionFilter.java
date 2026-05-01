package com.school.core.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AnomalyDetectionFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(AnomalyDetectionFilter.class);
	private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();
	private static final int THRESHOLD = 10;
	private static final long WINDOW_MS = 300000;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String clientIp = getClientIpAddress(request);
		String endpoint = request.getRequestURI();

		if (isAnonymousRequest(request) && isProtectedEndpoint(endpoint)) {
			String key = clientIp + ":" + endpoint;
			long now = System.currentTimeMillis();
			WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter());

			if (counter.isExpired(now)) {
				counter.reset(now);
			}
			int count = counter.increment();

			log.warn("ANONYMOUS_ACCESS_ATTEMPT | IP={} | ENDPOINT={} | COUNT={}",
					clientIp, endpoint, count);

			if (count > THRESHOLD) {
				response.setStatus(429);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	@Scheduled(fixedRate = 300000)
	public void cleanup() {
		long now = System.currentTimeMillis();
		counters.entrySet().removeIf(e -> e.getValue().isExpired(now));
	}

	private boolean isAnonymousRequest(@NonNull HttpServletRequest request) {
		org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		return auth == null || !auth.isAuthenticated() ||
				auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken;
	}

	private boolean isProtectedEndpoint(String endpoint) {
		return endpoint.startsWith("/students") || endpoint.startsWith("/sections") ||
				endpoint.startsWith("/admin");
	}

	private String getClientIpAddress(@NonNull HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
			return xForwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	private static class WindowCounter {
		private final AtomicInteger count = new AtomicInteger(0);
		private volatile long windowStart;

		WindowCounter() {
			this.windowStart = System.currentTimeMillis();
		}

		int increment() {
			return count.incrementAndGet();
		}

		boolean isExpired(long now) {
			return now - windowStart > WINDOW_MS;
		}

		void reset(long now) {
			count.set(0);
			windowStart = now;
		}
	}
}
