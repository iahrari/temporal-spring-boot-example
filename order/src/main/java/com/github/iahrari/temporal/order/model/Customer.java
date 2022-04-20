package com.github.iahrari.temporal.order.model;

import com.github.iahrari.temporal.api.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    private String customerId;
    private String phoneNumber;

    private Wallet wallet;
}
