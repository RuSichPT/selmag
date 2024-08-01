package com.github.rusichpt.feedback.controller;

import com.github.rusichpt.feedback.controller.payload.FavouriteProductResponse;
import com.github.rusichpt.feedback.controller.payload.NewFavouriteProductPayload;
import com.github.rusichpt.feedback.entity.FavouriteProduct;
import com.github.rusichpt.feedback.service.FavouriteProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class FavouriteProductRestControllerTest {

    @Mock
    FavouriteProductService favouriteProductService;

    @InjectMocks
    FavouriteProductRestController controller;

    @Test
    void findFavouriteProducts_ReturnsFavouriteProducts() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                new FavouriteProduct(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        ))).when(favouriteProductService).findFavouriteProducts("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.findFavouriteProducts(
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build()))))
                // then
                .expectNext(
                        new FavouriteProductResponse(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                        new FavouriteProductResponse(3, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
                )
                .verifyComplete();

        verify(favouriteProductService).findFavouriteProducts("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(favouriteProductService);
    }

    @Test
    void findFavouriteProductsByProductId_ReturnsFavouriteProducts() {
        // given
        doReturn(Mono.just(
                new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        )).when(favouriteProductService).findFavouriteProductByProductId(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.findFavouriteProductByProductId(
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())), 1))
                // then
                .expectNext(
                        new FavouriteProductResponse(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
                )
                .verifyComplete();

        verify(favouriteProductService)
                .findFavouriteProductByProductId(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(favouriteProductService);
    }

    @Test
    void addProductToFavourites_ReturnsCreatedFavouriteProduct() {
        // given
        doReturn(Mono.just(new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1,
                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(favouriteProductService).addProductToFavourites(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.addProductToFavourites(Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())),
                        Mono.just(new NewFavouriteProductPayload(1)),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity.created(URI.create("http://localhost/feedback-api/favourite-products/fe87eef6-cbd7-11ee-aeb6-275dac91de02"))
                        .body(new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"),
                                1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .verifyComplete();

        verify(favouriteProductService).addProductToFavourites(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(favouriteProductService);
    }

    @Test
    void removeProductFromFavourites_ReturnsNoContent() {
        // given
        doReturn(Mono.empty()).when(favouriteProductService)
                .removeProductFromFavourites(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.removeProductFromFavourites(Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                        .headers(headers -> headers.put("foo", "bar"))
                        .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())), 1))
                // then
                .expectNext(ResponseEntity.noContent().build())
                .verifyComplete();

        verify(favouriteProductService)
                .removeProductFromFavourites(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
    }
}