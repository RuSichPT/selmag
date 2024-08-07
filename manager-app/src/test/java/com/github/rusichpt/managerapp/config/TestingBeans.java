package com.github.rusichpt.managerapp.config;

import com.github.rusichpt.managerapp.client.RestClientProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestingBeans {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        return mock();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock();
    }

    @Bean
    @Primary
    public RestClientProductService testRestClientProductService(@Value("${selmag.services.catalogue.uri:http://localhost:54321}") String catalogueBaseUri) {
        // bug rest client and wiremock (HTTP_1_1)
        // I/O error on POST request for "http://localhost:54321/catalogue-api/products": EOF reached while reading
        // test createProduct_RequestIsInvalid_ReturnsNewProductPage
        // test createProduct_RequestIsValid_RedirectsToProductPage
        var client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(client);
        return new RestClientProductService(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestFactory(requestFactory)
                .build());
    }
}
