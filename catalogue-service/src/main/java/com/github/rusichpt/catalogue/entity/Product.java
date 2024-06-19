package com.github.rusichpt.catalogue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "catalogue", name = "t_product")
@NamedQueries(
        @NamedQuery(
                name = "Product.findAllByTitleIgnoringCase",
                query = "select p from Product p where p.title ilike :filter"
        )
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "c_title")
// Можно применять анотации валидации, но так не нужно
//    @NotNull
//    @Size(min = 3, max = 50)
    private String title;

    @Column(name = "c_details")
//    @Size(max = 1000)
    private String details;
}
