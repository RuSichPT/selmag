package com.github.rusichpt.feedback.controller;

import com.github.rusichpt.feedback.controller.payload.NewProductReviewPayload;
import com.github.rusichpt.feedback.controller.payload.ProductReviewResponse;
import com.github.rusichpt.feedback.entity.ProductReview;
import com.github.rusichpt.feedback.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewRestController {

    private final ProductReviewService productReviewService;

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReviewResponse> findProductReviewsByProductId(@PathVariable("productId") int productId) {
        return productReviewService.findProductReviewByProductId(productId)
                .map(productReview -> new ProductReviewResponse(
                        productReview.getProductId(),
                        productReview.getRating(),
                        productReview.getReview())); // todo mapper
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return payloadMono
                .flatMap(payload -> productReviewService.createProductReview(payload.productId(),
                        payload.rating(), payload.review()))
                .map(productReview -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}")
                                .build(productReview.getId()))
                        .body(productReview));
    }

}
