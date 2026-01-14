package com.digitalmoneyhouse.users_service.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignHeaderConfig {

    @Bean
    public RequestInterceptor userContextHeadersInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                // Si no hay request Http no se propaga nada
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            String userId = request.getHeader("X-User-Id");
            String email = request.getHeader("X-User-Email");

            if (userId != null && !userId.isBlank()) {
                template.header("X-User-Id", userId);
            }

            if (email != null && !email.isBlank()) {
                template.header("X-User-Email", email);
            }
        };
    }
}
