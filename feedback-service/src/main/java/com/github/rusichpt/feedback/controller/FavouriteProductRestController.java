package com.github.rusichpt.feedback.controller;

import com.github.rusichpt.feedback.controller.payload.FavouriteProductResponse;
import com.github.rusichpt.feedback.controller.payload.NewFavouriteProductPayload;
import com.github.rusichpt.feedback.entity.FavouriteProduct;
import com.github.rusichpt.feedback.service.FavouriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Flux<FavouriteProductResponse> findFavouriteProducts() {
        return favouriteProductService.findFavouriteProducts()
                .map(favouriteProduct -> new FavouriteProductResponse(favouriteProduct.getProductId())); //todo mapper
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavouriteProductResponse> findFavouriteProductByProductId(@PathVariable("productId") int productId) {
        return favouriteProductService.findFavouriteProductByProductId(productId)
                .map(favouriteProduct -> new FavouriteProductResponse(favouriteProduct.getProductId())); //todo mapper
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return payloadMono
                .flatMap(payload -> favouriteProductService.addProductToFavourites(payload.productId()))
                .map(favouriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favourite-products/{id}")
                                .build(favouriteProduct.getId()))
                        .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(@PathVariable("productId") int productId) {
        return favouriteProductService.removeProductFromFavourites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
