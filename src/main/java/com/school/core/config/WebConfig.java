package com.school.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/css/**")
                                .addResourceLocations("classpath:/static/css/")
                                .setCachePeriod(31536000);

                registry.addResourceHandler("/js/**")
                                .addResourceLocations("classpath:/static/js/")
                                .setCachePeriod(31536000);

                registry.addResourceHandler("/vendor/**")
                                .addResourceLocations("classpath:/static/vendor/")
                                .setCachePeriod(31536000);

                registry.addResourceHandler("/img/**")
                                .addResourceLocations("classpath:/static/img/")
                                .setCachePeriod(31536000);
        }
}