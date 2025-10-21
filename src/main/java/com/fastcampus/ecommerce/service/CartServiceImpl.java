package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.common.errors.BadRequestException;
import com.fastcampus.ecommerce.common.errors.ForbiddenAccessException;
import com.fastcampus.ecommerce.common.errors.ResourceNotFoundException;
import com.fastcampus.ecommerce.entity.Cart;
import com.fastcampus.ecommerce.entity.CartItem;
import com.fastcampus.ecommerce.entity.Product;
import com.fastcampus.ecommerce.model.CartItemResponse;
import com.fastcampus.ecommerce.repository.CartItemRepository;
import com.fastcampus.ecommerce.repository.CartRepository;
import com.fastcampus.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot add your own product to a cart.");
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingCartItem.isPresent()) {
            CartItem existingItem = existingCartItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        }else{
            CartItem mewItem = CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(productId)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cartItemRepository.save(mewItem);
        }
    }

    @Transactional
    @Override
    public void updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingCartItem.isEmpty()) {
            throw new ResourceNotFoundException("Product not found in cart");
        }

        CartItem item = existingCartItem.get();
        if (quantity < 0){
            cartItemRepository.deleteById(item.getCartItemId());
        }else{
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    @Override
    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Optional<CartItem> existingCartItem = cartItemRepository.findById(cartItemId);

        if (existingCartItem.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        CartItem item = existingCartItem.get();
        if(!item.getCartId().equals(cart.getCartId())){
            throw new ForbiddenAccessException("You cannot remove an item from a cart.");
        }

        cartItemRepository.deleteById(item.getCartItemId());
    }

    @Transactional
    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cartItemRepository.deleteAllByCartId(cart.getCartId());
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.getUserCartItems(userId);
        if(cartItems.isEmpty()){
            return Collections.emptyList();
        }

        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId).toList();

        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        return cartItems.stream()
                .map(cartItem -> {
                    Product product = productMap.get(cartItem.getProductId());
                    if(product == null){
                        throw new ResourceNotFoundException("Product not found for cart item");
                    }
                    return CartItemResponse.fromCartItemsAndProduct(cartItem, product);
                }).toList();
    }
}
