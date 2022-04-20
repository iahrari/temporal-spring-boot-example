package com.github.iahrari.temporal.driver.config;

import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.api.model.Wallet;
import com.github.iahrari.temporal.driver.model.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Configuration
public class DbConfig {
    @Bean
    public Set<Driver> driversDb(){
        Wallet w1 = new Wallet();
        w1.getTransactions().add(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .amount(BigDecimal.valueOf(1_000_000))
                        .status(Transaction.TransactionStatus.ADDITION)
                        .build()
        );

        Wallet w2 = new Wallet();
        w2.getTransactions().add(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .amount(BigDecimal.valueOf(1_000_000))
                        .status(Transaction.TransactionStatus.ADDITION)
                        .build()
        );

        return Set.of(
                Driver.builder()
                        .driverId("3")
                        .phoneNumber("09151602459")
                        .wallet(w1)
                        .build(),

                Driver.builder()
                        .driverId("4")
                        .phoneNumber("09215156620")
                        .wallet(w2)
                        .build()
        );
    }
}
