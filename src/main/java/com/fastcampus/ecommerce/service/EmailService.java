package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.entity.Order;

public interface EmailService {
    void sendPaymentSuccess(Order order);
}
