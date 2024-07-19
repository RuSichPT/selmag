package com.github.rusichpt.customerapp.client;

import com.github.rusichpt.customerapp.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAllProducts(String filter);

    Mono<Product> findProduct(int productId);
}
