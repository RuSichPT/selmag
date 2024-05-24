package com.github.rusichpt.managerapp.config;

import com.github.rusichpt.managerapp.client.RestClientProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBean {
    @Bean
    public RestClientProductService productService(@Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri){
        return new RestClientProductService(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
