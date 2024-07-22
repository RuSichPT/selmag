package com.github.rusichpt.customerapp.client;

import com.github.rusichpt.customerapp.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {

    Flux<ProductReview> findProductReviewByProductId(Integer productId);

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);
}
