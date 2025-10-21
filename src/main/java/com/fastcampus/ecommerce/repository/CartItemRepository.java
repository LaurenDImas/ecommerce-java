package com.fastcampus.ecommerce.repository;

import com.fastcampus.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query(value = """
        SELECT ci.* FROM cart_items ci
        WHERE EXISTS (
            SELECT 1 FROM carts c
            WHERE c.cart_id = ci.cart_id
            AND c.user_id = :userId
        )
        """, nativeQuery = true)
    List<CartItem> getUserCartItems(Long userId);

    @Query(value = """
        SELECT * FROM cart_items
        WHERE cart_items.cart_id = :cartId
        AND cart_items.product_id = :productId
        """, nativeQuery = true)
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query(value = """
        DELETE FROM cart_items
        WHERE cart_id = :cartId
        """, nativeQuery = true)
    void deleteAllByCartId(Long cartId);
}
