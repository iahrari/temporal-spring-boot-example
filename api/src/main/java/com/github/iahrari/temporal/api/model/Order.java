package com.github.iahrari.temporal.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private String id;
    private String customerId;
    private String driverId;
    private String phoneNumber;

    private Integer lat;
    private Integer lng;

    private BigDecimal amount;
    private OrderStatus status;
}
