package com.github.rusichpt.customerapp.config;

import com.github.rusichpt.customerapp.client.WebClientFavouriteProductService;
import com.github.rusichpt.customerapp.client.WebClientProductReviewService;
import com.github.rusichpt.customerapp.client.WebClientProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {
    @Bean
    @Scope("prototype")
    public WebClient.Builder selmagServicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                        authorizedClientRepository); // Добавляет все что нужно для oauth в заголовки
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    public WebClientProductService webClientProductService(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            WebClient.Builder builder) {
        return new WebClientProductService(builder
                .baseUrl(catalogueBaseUri)
                .build());
    }

    @Bean
    public WebClientFavouriteProductService webClientFavouriteProductService(
            @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder builder) {
        return new WebClientFavouriteProductService(builder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductReviewService webClientProductReviewService(
            @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder builder) {
        return new WebClientProductReviewService(builder
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
