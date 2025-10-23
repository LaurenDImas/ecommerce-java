package com.fastcampus.ecommerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING ("PENDING",  "Menunggu diproses"),
    SHIPPING ("SHIPPING",  "Pengiriman"),
    CANCELED  ("CANCELED",   "Ditolak/dibatalkan"),
    PAID ("PAID", "Sudah dibayar"),
    PAYMENT_FAILED  ("PAYMENT_FAILED",   "Pembayaran gagal");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
