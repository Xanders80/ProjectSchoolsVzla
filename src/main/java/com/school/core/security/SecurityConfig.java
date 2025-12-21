/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2025 [Tu Nombre o Empresa]
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.school.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private RateLimitingFilter rateLimitingFilter;
        
        @Autowired
        private com.school.core.filter.AuditLoggingFilter auditLoggingFilter;
        
        @Autowired
        private CustomAccessDeniedHandler customAccessDeniedHandler;
        
        @Autowired
        private AnomalyDetectionFilter anomalyDetectionFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.deny())
                                                .contentTypeOptions(contentTypeOptions -> {
                                                })
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                .maxAgeInSeconds(31536000)
                                                                .includeSubDomains(true))
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; object-src 'none'; base-uri 'self'")))
                                .addFilterBefore(anomalyDetectionFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(rateLimitingFilter, anomalyDetectionFilter.getClass())
                                .addFilterAfter(auditLoggingFilter, rateLimitingFilter.getClass())
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(
                                                                org.springframework.security.web.csrf.CookieCsrfTokenRepository
                                                                                .withHttpOnlyFalse())
                                                .ignoringRequestMatchers("/h2-console/**"))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                                                .sessionConcurrency(concurrency -> concurrency
                                                                .maximumSessions(1)
                                                                .maxSessionsPreventsLogin(false)
                                                                .sessionRegistry(sessionRegistry()))
                                                .sessionFixation().migrateSession())
                                .authorizeHttpRequests(auth -> auth
                                                // Recursos estáticos - PRIMERO
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/vendor/**",
                                                                "/webjars/**", "/favicon.ico", "/error/**")
                                                .permitAll()
                                                // Páginas públicas
                                                .requestMatchers("/login", "/register", "/forgot-password", "/404")
                                                .permitAll()
                                                // Herramientas de desarrollo
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                                .requestMatchers("/actuator/health").permitAll()
                                                // ENDPOINTS PROTEGIDOS - ORDEN ESPECÍFICO
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/students/**").hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers("/sections/**").hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers("/library/**").hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers("/actuator/**").hasRole("ADMIN")
                                                .requestMatchers("/reports/**").hasAnyRole("ADMIN", "DIRECTOR")
                                                .requestMatchers("/health/**").hasAnyRole("ADMIN", "DIRECTOR")
                                                .requestMatchers("/hr/**").hasAnyRole("ADMIN", "DIRECTOR")
                                                .requestMatchers("/bi/**").hasAnyRole("ADMIN", "DIRECTOR")
                                                .requestMatchers("/portal/**").hasAnyRole("PARENT", "ADMIN")
                                                .requestMatchers("/messages/**").authenticated()
                                                .requestMatchers("/notifications/broadcast").hasRole("ADMIN")
                                                .requestMatchers("/notifications/**").authenticated()
                                                // DENEGAR TODO LO DEMÁS
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .exceptionHandling(exceptions -> exceptions
                                                .accessDeniedHandler(customAccessDeniedHandler));

                return http.build();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
