package com.github.rusichpt.catalogue.controller;

import com.github.rusichpt.catalogue.controller.payload.NewProductPayload;
import com.github.rusichpt.catalogue.controller.payload.ProductResponse;
import com.github.rusichpt.catalogue.entity.Product;
import com.github.rusichpt.catalogue.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {
    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> findProducts(@RequestParam(name = "filter", required = false) String filter) {
        return StreamSupport.stream(productService.findAllProducts(filter).spliterator(),false)
                .map(product -> new ProductResponse(product.getId(), product.getTitle(), product.getDetails()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody NewProductPayload payload,
                                                         BindingResult bindingResult,
                                                         UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception){
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Product product = productService.createProduct(payload.title(), payload.details());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/catalogue-api/products/{productId}")
                            .build(Map.of("productId", product.getId())))
                    .body(new ProductResponse(product.getId(), product.getTitle(), product.getDetails()));
        }

    }
}
