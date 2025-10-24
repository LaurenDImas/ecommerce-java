package com.fastcampus.ecommerce.common;

import com.fastcampus.ecommerce.model.OrderStatus;

import java.util.EnumMap;
import java.util.Set;

public class OrderStateTransition {
    private static final EnumMap<OrderStatus, Set<OrderStatus>> VALID_TRANSACTION = new EnumMap<>(OrderStatus.class);

    static {
        VALID_TRANSACTION.put(OrderStatus.PENDING, Set.of(OrderStatus.CANCELLED, OrderStatus.PAID, OrderStatus.PAYMENT_FAILED));
        VALID_TRANSACTION.put(OrderStatus.PAID, Set.of(OrderStatus.SHIPPED));
        VALID_TRANSACTION.put(OrderStatus.CANCELLED, Set.of());
        VALID_TRANSACTION.put(OrderStatus.SHIPPED, Set.of());
        VALID_TRANSACTION.put(OrderStatus.PAYMENT_FAILED, Set.of());
    }

    public static boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        Set<OrderStatus> validNewStates = VALID_TRANSACTION.get(currentStatus);
        if (validNewStates == null){
            return false;
        }
        return validNewStates.contains(newStatus);
    }
}
