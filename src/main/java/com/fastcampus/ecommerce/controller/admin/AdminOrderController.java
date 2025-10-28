package com.fastcampus.ecommerce.controller.admin;

import com.fastcampus.ecommerce.common.PageUtil;
import com.fastcampus.ecommerce.common.SecurityUtils;
import com.fastcampus.ecommerce.common.errors.BadRequestException;
import com.fastcampus.ecommerce.entity.Order;
import com.fastcampus.ecommerce.model.*;
import com.fastcampus.ecommerce.service.OrderService;
import com.fastcampus.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/orders")
@SecurityRequirement(name = "Bearer")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable(name = "orderId") Long orderId){
        Order order = orderService.findByOrderId(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OrderResponse.fromOrder(order));
    }

    @GetMapping("")
    public ResponseEntity<PaginatedOrderResponse> findOrdersByUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "order_id,desc") String[] sort
    ){
        UserInfo userInfo = SecurityUtils.getCurrentUser();

        List<Sort.Order> sortOrder = PageUtil.parseSortOrderRequest(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrder));

        Page<OrderResponse> userOrders =  orderService.findOrdersByUserIdAndPageable(userInfo.getUser().getUserId(), pageable);
        PaginatedOrderResponse paginatedOrderResponse = orderService.convertOrderPage(userOrders);
        return ResponseEntity.ok(paginatedOrderResponse);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
