package com.school.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**")
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
				.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}
