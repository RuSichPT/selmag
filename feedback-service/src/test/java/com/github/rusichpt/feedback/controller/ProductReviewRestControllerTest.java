package com.github.rusichpt.feedback.controller;

import com.github.rusichpt.feedback.controller.payload.NewProductReviewPayload;
import com.github.rusichpt.feedback.controller.payload.ProductReviewResponse;
import com.github.rusichpt.feedback.entity.ProductReview;
import com.github.rusichpt.feedback.service.ProductReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewRestControllerTest {

    @Mock
    ProductReviewService productReviewService;

    @Mock
    ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    ProductReviewRestController controller;

    @Test
    void findProductReviewsByProductId_ReturnsProductReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new ProductReview(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1, 1,
                        "Отзыв №1", "user-1"),
                new ProductReview(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1, 3,
                        "Отзыв №2", "user-2"),
                new ProductReview(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1, 5,
                        "Отзыв №3", "user-3")
        ))).when(mongoTemplate).find(Query.query(Criteria.where("productId").is(1)), ProductReview.class);

        // when
        StepVerifier.create(controller.findProductReviewsByProductId(1,
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build()))))
                // then
                .expectNext(
                        new ProductReviewResponse(1, 1, "Отзыв №1", "user-1"),
                        new ProductReviewResponse(1, 3, "Отзыв №2", "user-2"),
                        new ProductReviewResponse(1, 5, "Отзыв №3", "user-3")
                )
                .verifyComplete();

        verify(mongoTemplate).find(Query.query(Criteria.where("productId").is(1)), ProductReview.class);
        verifyNoMoreInteractions(mongoTemplate);
    }

    @Test
    void createProductReview_ReturnsCreatedProductReview() {
        // given
        doReturn(Mono.just(new ProductReview(UUID.fromString("5a9ba234-cbd6-11ee-acab-5748ca6678b9"), 1, 4,
                "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(productReviewService)
                .createProductReview(1, 4, "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.createProductReview(
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())),
                        Mono.just(new NewProductReviewPayload(1, 4, "В целом норм")),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity.created(URI.create("http://localhost/feedback-api/product-reviews/5a9ba234-cbd6-11ee-acab-5748ca6678b9"))
                        .body(new ProductReview(UUID.fromString("5a9ba234-cbd6-11ee-acab-5748ca6678b9"), 1, 4,
                                "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .verifyComplete();

        verify(productReviewService)
                .createProductReview(1, 4, "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(productReviewService);
    }
}