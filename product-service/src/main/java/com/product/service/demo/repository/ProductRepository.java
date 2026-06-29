package com.product.service.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.product.service.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:category IS NULL OR p.category = :category)
        AND (:min IS NULL OR p.price >= :min)
        AND (:max IS NULL OR p.price <= :max)
    """)
    Page<Product> searchProducts(
            String name,
            String category,
            Double min,
            Double max,
            Pageable pageable
    );
}