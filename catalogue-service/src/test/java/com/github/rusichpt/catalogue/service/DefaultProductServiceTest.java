package com.github.rusichpt.catalogue.service;

import com.github.rusichpt.catalogue.entity.Product;
import com.github.rusichpt.catalogue.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    DefaultProductService service;

    @Test
    void findAllProducts_FilterIsNotSet_ReturnsProductsList() {
        // given
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i), "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(productRepository).findAll();

        // when
        var result = service.findAllProducts(null);

        // then
        assertEquals(products, result);

        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findAllProducts_FilterIsSet_ReturnsFilteredProductsList() {
        // given
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i), "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(productRepository).findAllByTitleLikeIgnoreCase("%товар%");

        // when
        var result = service.findAllProducts("товар");

        // then
        assertEquals(products, result);

        verify(productRepository).findAllByTitleLikeIgnoreCase("%товар%");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findProduct_ProductExists_ReturnsNotEmptyOptional() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        doReturn(Optional.of(product)).when(productRepository).findById(1);

        // when
        var result = service.findProduct(1);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(product, result.orElseThrow());

        verify(productRepository).findById(1);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findProduct_ProductDoesNotExist_ReturnsEmptyOptional() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        // when
        var result = service.findProduct(1);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository).findById(1);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        // given
        var title = "Новый товар";
        var details = "Описание нового товара";

        doReturn(new Product(1, "Новый товар", "Описание нового товара"))
                .when(productRepository).save(new Product(null, "Новый товар", "Описание нового товара"));

        // when
        var result = service.createProduct(title, details);

        // then
        assertEquals(new Product(1, "Новый товар", "Описание нового товара"), result);

        verify(productRepository).save(new Product(null, "Новый товар", "Описание нового товара"));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateProduct_ProductExists_UpdatesProduct() {
        // given
        var productId = 1;
        var product = new Product(1, "Новый товар", "Описание нового товара");
        var title = "Новое название";
        var details = "Новое описание";

        doReturn(Optional.of(product))
                .when(productRepository).findById(1);

        // when
        service.updateProduct(productId, title, details);

        // then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var productId = 1;
        var title = "Новое название";
        var details = "Новое описание";

        // when
        assertThrows(NoSuchElementException.class, () -> service
                .updateProduct(productId, title, details));

        // then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void deleteProduct_DeletesProduct() {
        // given
        var productId = 1;

        // when
        service.deleteProduct(productId);

        // then
        verify(productRepository).deleteById(productId);
        verifyNoMoreInteractions(productRepository);
    }
}