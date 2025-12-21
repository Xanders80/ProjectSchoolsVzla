package com.school.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI schoolManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("School Management System API")
                        .description("Sistema de Gestión Escolar - API Documentation")
                        .version("v1.0.0")
                        .license(new License()
                                .name("BSD 3-Clause License")
                                .url("https://opensource.org/licenses/BSD-3-Clause")));
    }
}