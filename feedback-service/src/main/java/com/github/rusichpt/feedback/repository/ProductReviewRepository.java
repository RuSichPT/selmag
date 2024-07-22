package com.github.rusichpt.feedback.repository;

import com.github.rusichpt.feedback.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewRepository {
    Mono<ProductReview> save(ProductReview productReview);
    Flux<ProductReview> findAllByProductId(int productId);
}
