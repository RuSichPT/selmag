package com.github.rusichpt.customerapp.config;

import com.github.rusichpt.customerapp.client.WebClientProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {
    @Bean
    public WebClientProductService webClientProductService(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri) {
        return new WebClientProductService(WebClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
