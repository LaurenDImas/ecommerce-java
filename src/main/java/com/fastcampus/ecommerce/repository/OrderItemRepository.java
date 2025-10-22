package com.fastcampus.ecommerce.repository;

import com.fastcampus.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query(value = """
        SELECT order_items.* FROM order_items
        WHERE EXISTS(
            SELECT 1 FROM orders 
             WHERE orders.order_id = order_items.order_id
             AND orders.user_id = :userId
             AND products.product_id = :productId
        )
        """, nativeQuery = true)
    List<OrderItem> findByUserAndProduct(Long userId, Long productId);

    @Query(value = """
           SELECT SUM(quantity*price) FROM order_items
           WHERE order_id = :orderId
        """, nativeQuery = true)
    Double calculateTotalPrice(Long orderId);
}
