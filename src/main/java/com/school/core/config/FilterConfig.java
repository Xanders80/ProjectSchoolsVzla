package com.school.core.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.school.core.filter.AuditLoggingFilter;
import com.school.core.security.AnomalyDetectionFilter;
import com.school.core.security.RateLimitingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuditLoggingFilter> auditLoggingFilterRegistration(AuditLoggingFilter filter) {
        FilterRegistrationBean<AuditLoggingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(RateLimitingFilter filter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AnomalyDetectionFilter> anomalyDetectionFilterRegistration(
            AnomalyDetectionFilter filter) {
        FilterRegistrationBean<AnomalyDetectionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
