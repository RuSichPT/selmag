package com.github.rusichpt.customerapp.config;

import com.github.rusichpt.customerapp.client.WebClientFavouriteProductService;
import com.github.rusichpt.customerapp.client.WebClientProductReviewService;
import com.github.rusichpt.customerapp.client.WebClientProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return mock();
    }

    @Bean
    @Primary
    public WebClientProductService mockWebClientProductService() {
        return new WebClientProductService(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientFavouriteProductService mockWebClientFavouriteProductService() {
        return new WebClientFavouriteProductService(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientProductReviewService mockWebClientProductReviewService() {
        return new WebClientProductReviewService(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return mock();
    }
}