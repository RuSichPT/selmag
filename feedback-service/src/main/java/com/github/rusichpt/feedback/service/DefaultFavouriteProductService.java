package com.github.rusichpt.feedback.service;

import com.github.rusichpt.feedback.entity.FavouriteProduct;
import com.github.rusichpt.feedback.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultFavouriteProductService implements FavouriteProductService {
    private final FavouriteProductRepository favouriteProductRepository;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId) {
        return favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId) {
        return favouriteProductRepository.deleteByProductId(productId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(int productId) {
        return favouriteProductRepository.findByProductId(productId);
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return favouriteProductRepository.findAll();
    }
}
