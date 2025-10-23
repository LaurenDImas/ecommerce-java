package com.fastcampus.ecommerce.model;

import com.fastcampus.ecommerce.entity.UserAddress;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingOrderRequest {

    private Long orderId;
    private ShippingRateRequest.Address fromAddress;
    private ShippingRateRequest.Address toAddress;
    private int totalWeightInGram;

    @Data
    @Builder
    public static class Address {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
    }
}
