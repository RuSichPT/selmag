package com.github.rusichpt.customerapp.controller;

import com.github.rusichpt.customerapp.client.FavouriteProductService;
import com.github.rusichpt.customerapp.client.ProductService;
import com.github.rusichpt.customerapp.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {
    private final ProductService productService;
    private final FavouriteProductService favouriteProductService;

    @GetMapping("list")
    public Mono<String> getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return productService.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> getFavouriteProductsPage(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return favouriteProductService.findFavouriteProducts()
                .map(FavouriteProduct::productId)
                .collectList()
                .flatMap(listProductId -> productService.findAllProducts(filter)
                        .filter(product -> listProductId.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)))
                .thenReturn("customer/products/favourites");
    }
}
