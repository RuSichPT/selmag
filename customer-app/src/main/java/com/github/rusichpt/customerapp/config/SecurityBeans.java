package com.github.rusichpt.customerapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityBeans {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(customizer -> customizer.anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())// Oauth2 Auth
                .oauth2Client(Customizer.withDefaults())
                .build();
    }
}