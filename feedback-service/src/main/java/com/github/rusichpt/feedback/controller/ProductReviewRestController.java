package com.github.rusichpt.feedback.controller;

import com.github.rusichpt.feedback.controller.payload.NewProductReviewPayload;
import com.github.rusichpt.feedback.controller.payload.ProductReviewResponse;
import com.github.rusichpt.feedback.entity.ProductReview;
import com.github.rusichpt.feedback.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
@Slf4j
public class ProductReviewRestController {

    private final ProductReviewService productReviewService;
    private final ReactiveMongoTemplate reactiveMongoTemplate; // для примера

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReviewResponse> findProductReviewsByProductId(@PathVariable("productId") int productId,
                                                                     Mono<JwtAuthenticationToken> principalMono) {
        return principalMono
                .flatMapMany(principal -> {
                    log.info("Principal claims:{}", principal.getToken().getClaims()); // для демонстрации
                    return reactiveMongoTemplate
                            .find(Query.query(Criteria.where("productId").is(productId)), ProductReview.class)
                            .map(productReview -> new ProductReviewResponse(
                                    productReview.getProductId(),
                                    productReview.getRating(),
                                    productReview.getReview(),
                                    productReview.getUserId())); // todo mapper
                });
//        return productReviewService.findProductReviewByProductId(productId)
//                .map(productReview -> new ProductReviewResponse(
//                        productReview.getProductId(),
//                        productReview.getRating(),
//                        productReview.getReview()));
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return authenticationTokenMono
                .flatMap(token -> payloadMono
                        .flatMap(payload -> productReviewService.createProductReview(payload.productId(),
                                payload.rating(), payload.review(), token.getToken().getSubject())))
                .map(productReview -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}")
                                .build(productReview.getId()))
                        .body(productReview));
    }
}
