package com.github.rusichpt.customerapp.service;

import com.github.rusichpt.customerapp.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {
    Mono<ProductReview> createProductReview(int productId, int rating, String review);
    Flux<ProductReview> findProductReviewByProductId(int productId);
}
