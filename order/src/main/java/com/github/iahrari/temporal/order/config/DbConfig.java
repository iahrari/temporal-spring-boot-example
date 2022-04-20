package com.github.iahrari.temporal.order.config;

import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.api.model.Wallet;
import com.github.iahrari.temporal.order.model.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Configuration
public class DbConfig {
    @Bean
    public Set<Customer> customersDb(){
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
                Customer.builder()
                        .customerId("1")
                        .phoneNumber("09151602459")
                        .wallet(w1)
                        .build(),

                Customer.builder()
                        .customerId("2")
                        .phoneNumber("09215156620")
                        .wallet(w2)
                        .build()
        );
    }
}
