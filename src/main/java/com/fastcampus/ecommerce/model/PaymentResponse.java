package com.fastcampus.ecommerce.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xendit.Xendit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentResponse {
    private String xenditInvoiceId;
    private String xenditExternalId;
    private BigDecimal amount;
    private String xenditInvoiceStatus;
    private String xenditPaymentUrl;
}
