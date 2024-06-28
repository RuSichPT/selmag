package com.github.rusichpt.catalogue.repository;

import com.github.rusichpt.catalogue.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // не создаем встраиваемую ДБ
@Sql("/sql/products.sql")
@Transactional
class ProductRepositoryIT {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findAllByTitleLikeIgnoreCase_ReturnsFilteredProductsList() {
        // given
        String filter = "%леб";

        // when
        Iterable<Product> products = productRepository.findAllByTitleLikeIgnoreCase(filter);

        // then
        assertEquals(List.of(new Product(4, "Хлеб", "Описание товара №4")), products);
    }
}