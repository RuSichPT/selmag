package com.github.rusichpt.customerapp.service;

import com.github.rusichpt.customerapp.entity.ProductReview;
import com.github.rusichpt.customerapp.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductReviewService implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review) {
        return productReviewRepository.save(
                new ProductReview(UUID.randomUUID(), productId, rating, review));
    }

    @Override
    public Flux<ProductReview> findProductReviewByProductId(int productId) {
        return productReviewRepository.findAllByProductId(productId);
    }
}
