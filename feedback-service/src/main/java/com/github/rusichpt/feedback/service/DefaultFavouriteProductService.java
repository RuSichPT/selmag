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
    public Mono<FavouriteProduct> addProductToFavourites(int productId, String userId) {
        return favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return favouriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(int productId, String userId) {
        return favouriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts(String userId) {
        return favouriteProductRepository.findAllByUserId(userId);
    }
}
