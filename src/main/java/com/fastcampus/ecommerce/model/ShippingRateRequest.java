package com.fastcampus.ecommerce.model;

import com.fastcampus.ecommerce.entity.UserAddress;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingRateRequest {
    private Address fromAddress;
    private Address toAddress;
    private BigDecimal totalWeightInGram;

    @Data
    @Builder
    public static class Address {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
    }

    public static Address fromUserAddress(UserAddress userAddress) {
        return Address.builder()
                .streetAddress(userAddress.getStreetAddress())
                .city(userAddress.getCity())
                .state(userAddress.getState())
                .postalCode(userAddress.getPostalCode())
                .build();
    }
}
