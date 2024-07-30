package com.github.rusichpt.customerapp.controller;

import com.github.rusichpt.customerapp.client.FavouriteProductService;
import com.github.rusichpt.customerapp.client.ProductReviewService;
import com.github.rusichpt.customerapp.client.ProductService;
import com.github.rusichpt.customerapp.client.exception.ClientBadRequestException;
import com.github.rusichpt.customerapp.controller.payload.NewProductReviewPayload;
import com.github.rusichpt.customerapp.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final FavouriteProductService favouriteProductService;
    private final ProductReviewService productReviewService;

    @ModelAttribute(value = "product", binding = false)
    public Mono<Product> product(@PathVariable("productId") int productId) {
        return productService.findProduct(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
                .doOnSuccess(token -> exchange.getAttributes()
                        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }

    @GetMapping
    public Mono<String> getProductPage(@PathVariable("productId") int productId, Model model) {
        model.addAttribute("inFavourite", false);
        return productReviewService.findProductReviewByProductId(productId)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .then(favouriteProductService.findFavouriteProductByProductId(productId)
                        .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true)))
                .thenReturn("customer/products/product");
    }

    @PostMapping("add-to-favourites")
    public Mono<String> addProductToFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductService.addProductToFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId))
                        .onErrorResume(exception -> {
                            log.error(exception.getMessage(), exception);
                            return Mono.just("redirect:/customer/products/%d".formatted(productId));
                        }));
    }

    @PostMapping("remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductService.removeProductFromFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(@PathVariable("productId") int id,
                                     NewProductReviewPayload payload,
                                     Model model) {
        return productReviewService.createProductReview(id, payload.rating(), payload.review())
                .thenReturn("redirect:/customer/products/%d".formatted(id))
                .onErrorResume(ClientBadRequestException.class, exception -> {
                    model.addAttribute("inFavourite", false);
                    model.addAttribute("payload", payload);
                    model.addAttribute("errors", exception.getErrors());
                    return favouriteProductService.findFavouriteProductByProductId(id)
                            .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true))
                            .thenReturn("customer/products/product");
                });
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        model.addAttribute("error", exception.getMessage());
        return "errors/404";
    }
}