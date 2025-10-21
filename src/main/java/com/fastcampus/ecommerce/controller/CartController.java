package com.fastcampus.ecommerce.controller;

import com.fastcampus.ecommerce.common.SecurityUtils;
import com.fastcampus.ecommerce.model.AddToCartRequest;
import com.fastcampus.ecommerce.model.CartItemResponse;
import com.fastcampus.ecommerce.model.UpdateCartItemRequest;
import com.fastcampus.ecommerce.model.UserInfo;
import com.fastcampus.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("carts")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<Void> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        UserInfo userInfo = SecurityUtils.getCurrentUser();

        cartService.addItemToCart(userInfo.getUser().getUserId(), request.getProductId(), request.getQuantity());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/items")
    public ResponseEntity<Void> updateCartItemQuantity(@Valid @RequestBody UpdateCartItemRequest request) {
        UserInfo userInfo = SecurityUtils.getCurrentUser();

        cartService.updateItemQuantity(userInfo.getUser().getUserId(), request.getProductId(), request.getQuantity());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long cartItemId) {
        UserInfo userInfo = SecurityUtils.getCurrentUser();

        cartService.removeItemFromCart(userInfo.getUser().getUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> deleteAllCartItems() {
        UserInfo userInfo = SecurityUtils.getCurrentUser();
        cartService.clearCart(userInfo.getUser().getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        UserInfo userInfo = SecurityUtils.getCurrentUser();
        List<CartItemResponse> cartItems = cartService.getCartItems(userInfo.getUser().getUserId());
        return ResponseEntity.ok(cartItems);
    }
}
