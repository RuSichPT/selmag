package com.github.rusichpt.feedback.repository;

import com.github.rusichpt.feedback.entity.FavouriteProduct;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemoryFavouriteProductRepository implements FavouriteProductRepository {
    private final List<FavouriteProduct> favouriteProducts = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Mono<FavouriteProduct> save(FavouriteProduct favouriteProduct) {
        favouriteProducts.add(favouriteProduct);
        return Mono.just(favouriteProduct);
    }

    @Override
    public Mono<Void> deleteByProductId(int productId) {
        favouriteProducts.removeIf(favouriteProduct -> favouriteProduct.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<FavouriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(favouriteProducts)
                .filter(favouriteProduct -> favouriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavouriteProduct> findAll() {
        return Flux.fromIterable(favouriteProducts);
    }
}
