package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.entity.Order;
import com.fastcampus.ecommerce.model.PaymentNotification;
import com.fastcampus.ecommerce.model.PaymentResponse;

import java.util.Optional;

public interface PaymentService {
    PaymentResponse create(Order order);
    PaymentResponse findByPaymentId(String paymentId);
    boolean verifyByPaymentId(String paymentId);
    void handleNotification(PaymentNotification paymentNotification);
}
