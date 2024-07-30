package com.github.rusichpt.feedback.service;

import com.github.rusichpt.feedback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(int productId, String userId);

    Mono<Void> removeProductFromFavourites(int productId, String userId);

    Mono<FavouriteProduct> findFavouriteProductByProductId(int productId, String userId);

    Flux<FavouriteProduct> findFavouriteProducts(String userId);
}
