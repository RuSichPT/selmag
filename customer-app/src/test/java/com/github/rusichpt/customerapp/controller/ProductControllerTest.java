package com.github.rusichpt.customerapp.controller;

import com.github.rusichpt.customerapp.client.FavouriteProductService;
import com.github.rusichpt.customerapp.client.ProductReviewService;
import com.github.rusichpt.customerapp.client.ProductService;
import com.github.rusichpt.customerapp.client.exception.ClientBadRequestException;
import com.github.rusichpt.customerapp.controller.payload.NewProductReviewPayload;
import com.github.rusichpt.customerapp.entity.FavouriteProduct;
import com.github.rusichpt.customerapp.entity.Product;
import com.github.rusichpt.customerapp.entity.ProductReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    ProductService productService;
    @Mock
    FavouriteProductService favouriteProductService;
    @Mock
    ProductReviewService productReviewService;
    @InjectMocks
    ProductController controller;
    @Test
    @DisplayName("Исключение NoSuchElementException должно быть транслировано в страницу errors/404")
    void handleNoSuchElementException_ReturnsErrors404() {
        // given
        var exception = new NoSuchElementException("Товар не найден");
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        // when
        String result = controller.handleNoSuchElementException(exception, model, response);

        // then
        assertEquals("errors/404", result);
        assertEquals("Товар не найден", model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void loadProduct_ProductExists_ReturnsNotEmptyMono() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        doReturn(Mono.just(product)).when(productService).findProduct(1);

        // when
        StepVerifier.create(controller.loadProduct(1))
                // then
                .expectNext(new Product(1, "Товар №1", "Описание товара №1"))
                .verifyComplete();

        verify(productService).findProduct(1);
        verifyNoMoreInteractions(productService); // не было вызовов
        verifyNoInteractions(favouriteProductService, productReviewService); // не было вызовов
    }

    @Test
    void loadProduct_ProductDoesNotExist_ReturnsMonoWithNoSuchElementException() {
        // given
        doReturn(Mono.empty()).when(productService).findProduct(1);

        // when
        StepVerifier.create(controller.loadProduct(1))
                // then
                .expectErrorMatches(exception -> exception instanceof NoSuchElementException e &&
                        e.getMessage().equals("customer.products.error.not_found"))
                .verify();

        verify(productService).findProduct(1);
        verifyNoMoreInteractions(productService);
        verifyNoInteractions(favouriteProductService, productReviewService);
    }
    @Test
    void getProductPage_ReturnsProductPage() {
        // given
        var model = new ConcurrentModel();
        var productReviews = List.of(
                new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"), 1, 5, "На пятёрочку","user-tester"),
                new ProductReview(UUID.fromString("849c3fac-cbaa-11ee-af68-737c6d37214a"), 1, 4, "Могло быть и лучше", "user-tester"));

        doReturn(Flux.fromIterable(productReviews)).when(productReviewService).findProductReviewByProductId(1);

        var favouriteProduct = new FavouriteProduct(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1, "user-tester");
        doReturn(Mono.just(favouriteProduct)).when(favouriteProductService).findFavouriteProductByProductId(1);

        // when
        StepVerifier.create(controller.getProductPage(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1")), model))
                // then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(productReviews, model.getAttribute("reviews"));
        assertEquals(true, model.getAttribute("inFavourite"));

        verify(productReviewService).findProductReviewByProductId(1);
        verify(favouriteProductService).findFavouriteProductByProductId(1);
        verifyNoMoreInteractions(productService, favouriteProductService);
        verifyNoInteractions(productService);
    }

    @Test
    void addProductToFavourites_RequestIsValid_RedirectsToProductPage() {
        // given
        doReturn(Mono.just(new FavouriteProduct(UUID.fromString("25ec67b4-cbac-11ee-adc8-4bd80e8171c4"), 1, "user-tester")))
                .when(favouriteProductService).addProductToFavourites(1);

        // when
        StepVerifier.create(controller.addProductToFavourites(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1"))))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductService).addProductToFavourites(1);
        verifyNoMoreInteractions(favouriteProductService);
        verifyNoInteractions(productReviewService, productService);
    }

    @Test
    void addProductToFavourites_RequestIsInvalid_RedirectsToProductPage() {
        // given
        doReturn(Mono.error(new ClientBadRequestException("Возникла какая-то ошибка", null,
                List.of("Какая-то ошибка"))))
                .when(favouriteProductService).addProductToFavourites(1);

        // when
        StepVerifier.create(controller.addProductToFavourites(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1"))))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductService).addProductToFavourites(1);
        verifyNoMoreInteractions(favouriteProductService);
        verifyNoInteractions(productReviewService, productService);
    }

    @Test
    void removeProductFromFavourites_RedirectsToProductPage() {
        // given
        doReturn(Mono.empty()).when(favouriteProductService).removeProductFromFavourites(1);

        // when
        StepVerifier.create(controller.removeProductFromFavourites(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1"))))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductService).removeProductFromFavourites(1);
        verifyNoMoreInteractions(favouriteProductService);
        verifyNoInteractions(productService, productReviewService);
    }

    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        doReturn(Mono.just(new ProductReview(UUID.fromString("86efa22c-cbae-11ee-ab01-679baf165fb7"), 1, 3, "Ну, на троечку...", "user-tester")))
                .when(productReviewService).createProductReview(1, 3, "Ну, на троечку...");

        // when
        StepVerifier.create(controller.createReview(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1")),
                        new NewProductReviewPayload(3, "Ну, на троечку..."), model, response))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        assertNull(response.getStatusCode());

        verify(productReviewService).createProductReview(1, 3, "Ну, на троечку...");
        verifyNoMoreInteractions(productReviewService);
        verifyNoInteractions(productService, favouriteProductService);
    }

    @Test
    void createReview_RequestIsInvalid_ReturnsProductPageWithPayloadAndErrors() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        var favouriteProduct = new FavouriteProduct(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1, "user-tester");
        doReturn(Mono.just(favouriteProduct)).when(favouriteProductService).findFavouriteProductByProductId(1);

        doReturn(Mono.error(new ClientBadRequestException("Возникла какая-то ошибка", null, List.of("Ошибка 1", "Ошибка 2"))))
                .when(productReviewService).createProductReview(1, null, "Очень длинный отзыв");

        // when
        StepVerifier.create(controller.createReview(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1")),
                        new NewProductReviewPayload(null, "Очень длинный отзыв"), model, response))
                // then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(true, model.getAttribute("inFavourite"));
        assertEquals(new NewProductReviewPayload(null, "Очень длинный отзыв"), model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(productReviewService).createProductReview(1, null, "Очень длинный отзыв");
        verify(favouriteProductService).findFavouriteProductByProductId(1);
        verifyNoMoreInteractions(productReviewService, favouriteProductService);
        verifyNoInteractions(productService);
    }
}