package com.github.iahrari.temporal.driver.model;

import com.github.iahrari.temporal.api.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    private String driverId;
    private String phoneNumber;

    private Wallet wallet;
}
