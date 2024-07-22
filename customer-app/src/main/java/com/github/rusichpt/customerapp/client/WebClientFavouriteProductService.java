package com.github.rusichpt.customerapp.client;

import com.github.rusichpt.customerapp.client.exception.ClientBadRequestException;
import com.github.rusichpt.customerapp.controller.payload.NewFavouriteProductPayload;
import com.github.rusichpt.customerapp.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class WebClientFavouriteProductService implements FavouriteProductService {

    private final WebClient webClient;

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return webClient
                .get()
                .uri("/feedback-api/favourite-products")
                .retrieve()
                .bodyToFlux(FavouriteProduct.class);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(int productId) {
        return webClient
                .get()
                .uri("/feedback-api/favourite-products/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId) {
        return webClient
                .post()
                .uri("/feedback-api/favourite-products")
                .bodyValue(new NewFavouriteProductPayload(productId))
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new ClientBadRequestException(exception,
                                ((List<String>) exception.getResponseBodyAs(ProblemDetail.class)
                                        .getProperties().get("errors"))));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId) {
        return webClient
                .delete()
                .uri("/feedback-api/favourite-products/by-product-id/{productId}", productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
