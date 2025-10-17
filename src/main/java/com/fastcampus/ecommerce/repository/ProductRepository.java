package com.fastcampus.ecommerce.repository;

import com.fastcampus.ecommerce.entity.Product;
import com.fastcampus.ecommerce.model.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Product> findByNamePageable(@Param("name") String name, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT p.* FROM product_categories pc
        JOIN products p  ON p.id = pc.product_id
        JOIN categories c ON pc.category_id = c.id
        WHERE c.name = :categoryName
        """, nativeQuery = true)
    List<Product> findByCategory(@Param("categoryName") String categoryName);

    @Query(value = """
        SELECT * FROM products
        """, nativeQuery = true)
    Page<Product> findByPageable(Pageable pageable);
}
