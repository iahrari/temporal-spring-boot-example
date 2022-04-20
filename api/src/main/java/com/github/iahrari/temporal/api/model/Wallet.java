package com.github.iahrari.temporal.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {
    private Set<Transaction> transactions = new HashSet<>();

    public BigDecimal total(){
        return transactions.stream()
                .map(tx -> tx.getAmount()
                        .multiply(BigDecimal.valueOf(tx.getStatus() == Transaction.TransactionStatus.ADDITION? 1: -1)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
