package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.common.errors.ResourceNotFoundException;
import com.fastcampus.ecommerce.entity.*;
import com.fastcampus.ecommerce.enums.OrderStatus;
import com.fastcampus.ecommerce.model.CheckoutRequest;
import com.fastcampus.ecommerce.model.OrderItemResponse;
import com.fastcampus.ecommerce.model.ShippingRateRequest;
import com.fastcampus.ecommerce.model.ShippingRateResponse;
import com.fastcampus.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductRepository productRepository;
    private final ShippingService shippingService;

    private final BigDecimal TAX_RATE = BigDecimal.valueOf(0.03);

    @Override
    @Transactional
    public Order checkout(CheckoutRequest checkoutRequest) {
        List<CartItem> selectedItems = cartItemRepository.findAllById(
                checkoutRequest.getSelectedCartItemIds());
        if(selectedItems.isEmpty()) {
            throw new ResourceNotFoundException("No cart item selected");
        }

        UserAddress shippingAddress = userAddressRepository.findById(checkoutRequest.getUserAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("User address not found"));

        Order newOrder = Order.builder()
                .userId(checkoutRequest.getUserId())
                .status(OrderStatus.PENDING.getCode())
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .taxFee(BigDecimal.ZERO)
                .subtotal(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = selectedItems.stream()
                .map(cartItem -> {
                    return OrderItem.builder()
                            .orderId(savedOrder.getOrderId())
                            .productId(cartItem.getProductId())
                            .quantity(cartItem.getQuantity())
                            .price(cartItem.getPrice())
                            .userAddressId(shippingAddress.getUserAddressId())
                            .build();
                }).toList();

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(selectedItems);

        BigDecimal subtotal = orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = orderItems.stream()
                .map(orderItem -> {
                    Optional<Product> product = productRepository.findById(orderItem.getProductId());
                    if (product.isEmpty()){
                        return BigDecimal.ZERO;
                    }

                    Optional<UserAddress> sellerAddress = userAddressRepository.findByUserIdAndIsDefaultTrue(product.get().getUserId());
                    if (sellerAddress.isEmpty()){
                        return BigDecimal.ZERO;
                    }

                    BigDecimal totalWeight = product.get().getWeight().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    ShippingRateRequest rateRequest = ShippingRateRequest.builder()
                            .totalWeightInGram(totalWeight)
                            .fromAddress(ShippingRateRequest.fromUserAddress(sellerAddress.get()))
                            .toAddress(ShippingRateRequest.fromUserAddress(shippingAddress))
                            .build();
                    ShippingRateResponse rateResponse = shippingService.calculateShippingRate(rateRequest);
                    return rateResponse.getShippingFee();
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxFee = subtotal.multiply(TAX_RATE);
        BigDecimal totalAmount = subtotal.add(shippingFee).add(taxFee);

        savedOrder.setSubtotal(subtotal);
        savedOrder.setShippingFee(shippingFee);
        savedOrder.setTaxFee(taxFee);
        savedOrder.setTotalAmount(totalAmount);

        return orderRepository.save(savedOrder);
    }

    @Override
    public Optional<Order> findByOrderId(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            throw new IllegalStateException("Order status must be PENDING");
        }

        order.setStatus(OrderStatus.CANCELED.getCode());
        orderRepository.save(order);
    }

    @Override
    public List<OrderItemResponse> findOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = orderItems.stream()
                .map(OrderItem::getProductId).toList();

        List<Long> shippingAddressIds = orderItems.stream()
                .map(OrderItem::getUserAddressId).toList();

        List<Product> products = productRepository.findAllById(productIds);
        List<UserAddress> shippingAddress = userAddressRepository.findAllById(shippingAddressIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        Map<Long, UserAddress> userAddressMap = shippingAddress.stream()
                .collect(Collectors.toMap(UserAddress::getUserAddressId, Function.identity()));

        return orderItems.stream()
                .map(orderItem -> {
                    Product product = Optional.ofNullable(productMap.get(orderItem.getProductId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                    UserAddress userAddress = Optional.ofNullable(userAddressMap.get(orderItem.getUserAddressId()))
                            .orElseThrow(() -> new ResourceNotFoundException("UserAddress not found"));

                    return OrderItemResponse.fromOrderItemProductAddress(orderItem, product, userAddress);
                })
                .toList();
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Double calculateOrderTotal(Long orderId) {
        return orderItemRepository.calculateTotalPrice(orderId);
    }
}
