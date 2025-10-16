package com.fastcampus.ecommerce.repository;

import com.fastcampus.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
        SELECT * FROM products
        WHERE lower(name) LIKE :name
        """, nativeQuery = true)
    List<Product> findByName(@Param("name") String name);

    @Query(value = """
        SELECT DISTINCT p.* FROM products p
        JOIN product_categories pc ON p.id = pc.product_id
        JOIN categories c ON pc.category_id = c.id
        WHERE c.name = :categoryName
        """, nativeQuery = true)
    List<Product> findByCategory(@Param("categoryName") String categoryName);
}
