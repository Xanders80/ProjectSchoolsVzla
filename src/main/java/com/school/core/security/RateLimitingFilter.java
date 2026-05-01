package com.school.core.security;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter implements Filter {

	private final ConcurrentHashMap<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();
	private static final int MAX_REQUESTS = 100;
	private static final long TIME_WINDOW = 60000;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String clientIP = getClientIP(httpRequest);
		long currentTime = System.currentTimeMillis();

		List<Long> timestamps = requestTimestamps.computeIfAbsent(
				clientIP, k -> new CopyOnWriteArrayList<>());

		timestamps.removeIf(ts -> currentTime - ts > TIME_WINDOW);

		if (timestamps.size() >= MAX_REQUESTS) {
			httpResponse.setStatus(429);
			httpResponse.getWriter().write("Rate limit exceeded");
			return;
		}

		timestamps.add(currentTime);
		chain.doFilter(request, response);
	}

	private String getClientIP(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
