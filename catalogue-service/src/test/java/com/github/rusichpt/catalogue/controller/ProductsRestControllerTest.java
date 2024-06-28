package com.github.rusichpt.catalogue.controller;

import com.github.rusichpt.catalogue.controller.payload.NewProductPayload;
import com.github.rusichpt.catalogue.controller.payload.ProductResponse;
import com.github.rusichpt.catalogue.entity.Product;
import com.github.rusichpt.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsRestControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductsRestController controller;

    @Test
    void findProduct_ReturnsProductsList() {
        // given
        var filter = "товар";

        doReturn(List.of(new Product(1, "Первый товар", "Описание первого товара"),
                new Product(2, "Второй товар", "Описание второго товара")))
                .when(productService).findAllProducts("товар");

        // when
        var result = controller.findProducts(filter);

        // then
        assertEquals(List.of(new ProductResponse(1, "Первый товар", "Описание первого товара"),
                new ProductResponse(2, "Второй товар", "Описание второго товара")), result);
    }

    @Test
    void createProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var payload = new NewProductPayload("Новое название", "Новое описание");
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(new Product(1, "Новое название", "Новое описание"))
                .when(productService).createProduct("Новое название", "Новое описание");

        // when
        var result = controller.createProduct(payload, bindingResult, uriComponentsBuilder);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/catalogue-api/products/1"), result.getHeaders().getLocation());
        assertEquals(new ProductResponse(1, "Новое название", "Новое описание"), result.getBody());

        verify(productService).createProduct("Новое название", "Новое описание");
        verifyNoMoreInteractions(productService);
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new NewProductPayload("   ", null);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> controller.createProduct(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new NewProductPayload("   ", null);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> controller.createProduct(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }
}