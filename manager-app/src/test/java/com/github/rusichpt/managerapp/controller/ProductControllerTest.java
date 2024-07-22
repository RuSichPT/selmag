package com.github.rusichpt.managerapp.controller;

import com.github.rusichpt.managerapp.client.exception.BadRequestException;
import com.github.rusichpt.managerapp.client.ProductService;
import com.github.rusichpt.managerapp.controller.payload.UpdateProductPayload;
import com.github.rusichpt.managerapp.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductController controller;

    @Test
    void product_ProductExists_ReturnsProduct() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        doReturn(Optional.of(product)).when(productService).findProduct(1);

        // when
        Product result = controller.product(1);

        // then
        assertEquals(product, result);

        verify(productService).findProduct(1);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void product_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given

        // when
        var exception = assertThrows(NoSuchElementException.class, () -> controller.product(1));

        // then
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());

        verify(productService).findProduct(1);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getProduct_ReturnsProductPage() {
        // given

        // when
        String result = controller.getProductPage();

        // then
        assertEquals("catalogue/products/product", result);

        verifyNoInteractions(productService);
    }

    @Test
    void getProductEditPage_ReturnsProductEditPage() {
        // given

        // when
        String result = controller.getProductEditPage();

        // then
        assertEquals("catalogue/products/edit", result);

        verifyNoInteractions(productService);
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        var payload = new UpdateProductPayload("Новое название", "Новое описание");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        // when
        String result = controller.updateProduct(product, payload, model, response);

        // then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(productService).updateProduct(1, "Новое название", "Новое описание");
        verifyNoMoreInteractions(productService);
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductEditPage() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        var payload = new UpdateProductPayload("   ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(productService).updateProduct(1, "   ", null);

        // when
        String result = controller.updateProduct(product, payload, model, response);

        // then
        assertEquals("catalogue/products/edit", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productService).updateProduct(1, "   ", null);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void deleteProduct_RedirectsToProductsListPage() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        // when
        String result = controller.deleteProduct(product);

        // then
        assertEquals("redirect:/catalogue/products/list", result);

        verify(productService).deleteProduct(1);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void handleNoSuchElementException_Returns404ErrorPage() {
        // given
        var exception = new NoSuchElementException("error");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        var locale = Locale.of("ru");

        doReturn("Ошибка").when(messageSource)
                .getMessage("error", new Object[0], "error", Locale.of("ru"));

        // when
        String result = controller.handleNoSuchElementException(exception, model, response, locale);

        // then
        assertEquals("errors/404",  result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(messageSource).getMessage("error", new Object[0], "error", Locale.of("ru"));
        verifyNoMoreInteractions(messageSource);
        verifyNoInteractions(productService);
    }
}
