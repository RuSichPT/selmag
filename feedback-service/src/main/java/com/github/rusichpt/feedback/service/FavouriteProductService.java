package com.github.rusichpt.feedback.service;

import com.github.rusichpt.feedback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(int productId);

    Mono<Void> removeProductFromFavourites(int productId);

    Mono<FavouriteProduct> findFavouriteProductByProductId(int productId);

    Flux<FavouriteProduct> findFavouriteProducts();
}
