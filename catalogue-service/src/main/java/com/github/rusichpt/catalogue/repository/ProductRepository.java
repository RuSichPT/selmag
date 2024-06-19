package com.github.rusichpt.catalogue.repository;

import com.github.rusichpt.catalogue.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends CrudRepository<Product, Integer> {
    Iterable<Product> findAllByTitleLikeIgnoreCase(String filter); //select * from catalogue.t_product where upper(c_title) like upper(:filter)

    @Query(value = "select p from Product p where p.title ilike :filter")
    Iterable<Product> findAllByTitleLikeIgnoreCase1(@Param("filter") String filter);  // JPQL

    @Query(value = "select * from catalogue.t_product where c_title ilike :filter", nativeQuery = true)
    Iterable<Product> findAllByTitleLikeIgnoreCase2(@Param("filter") String filter);  // SQL

    @Query(name = "Product.findAllByTitleIgnoringCase", nativeQuery = true)
    Iterable<Product> findAllByTitleLikeIgnoreCase3(@Param("filter") String filter);  // SQL
}
