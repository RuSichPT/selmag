package com.github.rusichpt.managerapp.client;

import com.github.rusichpt.managerapp.client.exception.BadRequestException;
import com.github.rusichpt.managerapp.controller.payload.NewProductPayload;
import com.github.rusichpt.managerapp.controller.payload.UpdateProductPayload;
import com.github.rusichpt.managerapp.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class RestClientProductService implements ProductService {
    private static final ParameterizedTypeReference<List<Product>> PRODUCT_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };
    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String filter) {
        return restClient
                .get()
                .uri("/catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .body(PRODUCT_TYPE_REFERENCE);
    }

    @Override
    public Product createProduct(String title, String details) {
        try {
            return restClient
                    .post()
                    .uri("/catalogue-api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewProductPayload(title, details)) // нужны общая библиотека с дто (NewProductPayload)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        try {
            return Optional.ofNullable(restClient
                    .get()
                    .uri("/catalogue-api/products/{productId}", productId)
                    .retrieve()
                    .body(Product.class));
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        }
    }

    @Override
    public void updateProduct(int productId, String title, String details) {
        try {
            restClient
                    .patch()
                    .uri("/catalogue-api/products/{productId}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateProductPayload(title, details)) // нужны общая библиотека с дто (NewProductPayload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            restClient
                    .delete()
                    .uri("/catalogue-api/products/{productId}", productId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
