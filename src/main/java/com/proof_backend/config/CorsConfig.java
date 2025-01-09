package com.proof_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all origins, methods, and headers
        registry.addMapping("/api/v1/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS")  // Allow all HTTP methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(false);
    }

}

