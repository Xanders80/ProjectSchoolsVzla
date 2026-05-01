package com.school.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

	@Bean
	public WebSecurityCustomizer devSecurityCustomizer() {
		return web -> web.ignoring()
				.requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**");
	}
}
