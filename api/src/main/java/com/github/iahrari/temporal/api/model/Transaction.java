package com.github.iahrari.temporal.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Transaction {
    private String id;
    private BigDecimal amount;
    private TransactionStatus status;

    public enum TransactionStatus {
        DEDUCTION, ADDITION;

        public TransactionStatus reverse(TransactionStatus status){
            return status == DEDUCTION? ADDITION: DEDUCTION;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
