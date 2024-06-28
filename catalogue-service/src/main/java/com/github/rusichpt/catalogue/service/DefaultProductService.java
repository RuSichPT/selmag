package com.github.rusichpt.catalogue.service;

import com.github.rusichpt.catalogue.entity.Product;
import com.github.rusichpt.catalogue.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Iterable<Product> findAllProducts(String filter) {
        if (Objects.nonNull(filter) && !filter.isBlank()) {
//            return productRepository.findAllByTitleLikeIgnoreCase3("%" + filter + "%");
//            return productRepository.findAllByTitleLikeIgnoreCase2("%" + filter + "%");
//            return productRepository.findAllByTitleLikeIgnoreCase1("%" + filter + "%");
            return productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
        } else {
            return productRepository.findAll();
        }
    }

    @Override
    @Transactional
    public Product createProduct(String title, String details) {
        return productRepository.save(new Product(null, title, details));
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        return productRepository.findById(productId);
    }

    @Override
    @Transactional
    public void updateProduct(int productId, String title, String details) {
        productRepository.findById(productId)
                .ifPresentOrElse(product -> {
                    product.setTitle(title);
                    product.setDetails(details);
//                    productRepository.save(product); // Сохраняем в рамках транзакции @Transactional
                }, () -> {
                    throw new NoSuchElementException();
                });
    }

    @Override
    @Transactional
    public void deleteProduct(int productId) {
        productRepository.deleteById(productId);
    }

}
