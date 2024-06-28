package com.github.rusichpt.managerapp.controller;

import com.github.rusichpt.managerapp.client.BadRequestException;
import com.github.rusichpt.managerapp.client.ProductService;
import com.github.rusichpt.managerapp.controller.payload.NewProductPayload;
import com.github.rusichpt.managerapp.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsproductsController")
class ProductsControllerTest {
    @Mock
    ProductService productService;
    @InjectMocks
    ProductsController productsController;

    @Test
    void getProductsList_ReturnsProductsListPage() {
        // given
        var model = new ConcurrentModel();
        var filter = "товар";
        var authentication = new TestingAuthenticationToken("manager", "password");
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i),
                        "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(productService).findAllProducts(filter);

        // when
        var result = productsController.getProductsList(model, filter, authentication);

        // then
        assertEquals("catalogue/products/list", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(products, model.getAttribute("products"));
    }

    @Test
    void getNewProductPage_ReturnsNewProductPage () {
        // given

        // when
        var result = productsController.getNewProductPage();

        // then
        assertEquals("catalogue/products/new_product", result);
    }
    @Test
    // можно через DisplayName или через название метода / условие при котором тестируется метод / ожидаемый результат
    @DisplayName("createProduct создаст новый товар и перенаправит на страницу товара")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var payload = new NewProductPayload("Новый товар", "Описание нового товара");
        var model = new ConcurrentModel();

        doReturn(new Product(1, "Новый товар", "Описание нового товара"))
                .when(productService)
                .createProduct("Новый товар", "Описание нового товара");
//                .createProduct(eq("Новый товар"), any()); можно так
//                .createProduct(notNull(), any()); можно так

        // when
        String result = productsController.createProduct(payload, model);

        // then
        assertEquals("redirect:/catalogue/products/1", result);
        verify(productService).createProduct("Новый товар", "Описание нового товара");
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("createProduct вернет страницу с ошибками если запрос не валиден")
    void createProduct_RequestIsInvalid_ReturnsProductFormWithErrors() {
        // given
        var payload = new NewProductPayload("   ", null);
        var model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(productService)
                .createProduct("   ", null);

        // when
        String result = productsController.createProduct(payload, model);

        // then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(productService).createProduct("   ", null);
        verifyNoMoreInteractions(productService);
    }
}