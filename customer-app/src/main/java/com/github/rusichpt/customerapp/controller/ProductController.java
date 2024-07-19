package com.github.rusichpt.customerapp.controller;

import com.github.rusichpt.customerapp.client.ProductService;
import com.github.rusichpt.customerapp.controller.payload.NewProductReviewPayload;
import com.github.rusichpt.customerapp.entity.Product;
import com.github.rusichpt.customerapp.service.FavouriteProductService;
import com.github.rusichpt.customerapp.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
public class ProductController {
    private final ProductService productService;
    private final FavouriteProductService favouriteProductService;
    private final ProductReviewService productReviewService;

    @ModelAttribute(value = "product", binding = false)
    public Mono<Product> product(@PathVariable("productId") int productId) {
        return productService.findProduct(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
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
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductService.removeProductFromFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(@PathVariable("productId") int productId,
                                     @Valid NewProductReviewPayload payload,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("inFavourite", false);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return favouriteProductService.findFavouriteProductByProductId(productId)
                    .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true))
                    .thenReturn("customer/products/product");
        } else {
            return productReviewService.createProductReview(productId, payload.rating(), payload.review())
                    .thenReturn("redirect:/customer/products/%d".formatted(productId));
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        model.addAttribute("error", exception.getMessage());
        return "errors/404";
    }
}