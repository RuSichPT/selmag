package com.github.rusichpt.customerapp.client;

import com.github.rusichpt.customerapp.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class WebClientProductService implements ProductService {
    private final WebClient webClient;

    @Override
    public Flux<Product> findAllProducts(String filter) {
        return webClient
                .get()
                .uri("/catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findProduct(int productId) {
        return webClient
                .get()
                .uri("/catalogue-api/products/{productId}", productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
