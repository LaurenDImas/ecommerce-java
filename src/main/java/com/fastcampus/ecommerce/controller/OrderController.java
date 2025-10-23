package com.fastcampus.ecommerce.controller;

import com.fastcampus.ecommerce.common.SecurityUtils;
import com.fastcampus.ecommerce.common.errors.ResourceNotFoundException;
import com.fastcampus.ecommerce.entity.Order;
import com.fastcampus.ecommerce.model.CheckoutRequest;
import com.fastcampus.ecommerce.model.OrderItemResponse;
import com.fastcampus.ecommerce.model.OrderResponse;
import com.fastcampus.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
@SecurityRequirement(name = "Bearer")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest){
        checkoutRequest.setUserId(SecurityUtils.getCurrentUser().getUser().getUserId());
        Order order = orderService.checkout(checkoutRequest);
        OrderResponse orderResponse = OrderResponse.fromOrder(order);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable(name = "orderId") Long orderId){

        Order order = orderService.findByOrderId(orderId).orElse(null);
        if (order == null) {
            return  ResponseEntity.notFound().build();
        }
        if (!order.getUserId().equals(SecurityUtils.getCurrentUser().getUser().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(OrderResponse.builder().build());
        }
        return ResponseEntity.ok(OrderResponse.fromOrder(order));
    }

    @GetMapping("")
    public ResponseEntity<List<OrderResponse>> findAllOrdersByUserId(){
        List<Order> userOrders = orderService.findOrdersByUserId(SecurityUtils.getCurrentUser().getUser().getUserId());
        List<OrderResponse> orderResponses =  userOrders.stream().map(OrderResponse::fromOrder).toList();
        return ResponseEntity.ok(orderResponses);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> findOrderItems(@PathVariable Long orderId){
        List<OrderItemResponse> orderItemResponses = orderService.findOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItemResponses);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
                                                  @RequestParam String newStatus){
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/total")
    public ResponseEntity<Double> calculateOrderTotal(@PathVariable Long orderId){
        double orderTotal = orderService.calculateOrderTotal(orderId);
        return ResponseEntity.ok(orderTotal);
    }
}
