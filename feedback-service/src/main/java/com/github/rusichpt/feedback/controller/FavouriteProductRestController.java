package com.github.rusichpt.feedback.controller;

import com.github.rusichpt.feedback.controller.payload.FavouriteProductResponse;
import com.github.rusichpt.feedback.controller.payload.NewFavouriteProductPayload;
import com.github.rusichpt.feedback.entity.FavouriteProduct;
import com.github.rusichpt.feedback.service.FavouriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductRestController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping
    public Flux<FavouriteProductResponse> findFavouriteProducts(Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono
                .flatMapMany(token -> favouriteProductService.findFavouriteProducts(token.getToken().getSubject()))
                .map(favouriteProduct -> new FavouriteProductResponse(favouriteProduct.getProductId())); //todo mapper
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavouriteProductResponse> findFavouriteProductByProductId(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") int productId) {
        return authenticationTokenMono
                .flatMap(token -> favouriteProductService.findFavouriteProductByProductId(productId, token.getToken().getSubject()))
                .map(favouriteProduct -> new FavouriteProductResponse(favouriteProduct.getProductId())); //todo mapper
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return Mono.zip(authenticationTokenMono, payloadMono)
                .flatMap(tuple -> favouriteProductService
                        .addProductToFavourites(tuple.getT2().productId(), tuple.getT1().getToken().getSubject()))
                .map(favouriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favourite-products/{id}")
                                .build(favouriteProduct.getId()))
                        .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") int productId) {
        return authenticationTokenMono
                .flatMap(token -> favouriteProductService.removeProductFromFavourites(productId, token.getToken().getSubject()))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
