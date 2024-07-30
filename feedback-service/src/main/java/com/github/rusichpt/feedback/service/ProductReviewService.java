package com.github.rusichpt.feedback.service;

import com.github.rusichpt.feedback.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {
    Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId);
    Flux<ProductReview> findProductReviewByProductId(int productId);
}
