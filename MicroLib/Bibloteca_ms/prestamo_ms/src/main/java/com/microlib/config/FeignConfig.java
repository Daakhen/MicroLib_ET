package com.microlib.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(HttpServletRequest request) {
        return template -> {
            String token = request.getHeader("Authorization");

            if (token != null) {
                template.header("Authorization", token);
            }
        };
    }
}