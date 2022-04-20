package com.github.iahrari.temporal.order.controller;

import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.order.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@RequestMapping("/api/v1/customer")
@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final Set<Customer> customersDb;

    @PutMapping("/{id}/{amount}")
    public void chargeWallet(@PathVariable Long amount, @PathVariable String id){
        var customer = customersDb.stream()
                .filter(c -> c.getCustomerId().equals(id))
                .findAny().orElseThrow();
        amount.getClass();
        var transaction = Transaction.builder()
                .status(Transaction.TransactionStatus.ADDITION)
                .amount(BigDecimal.valueOf(amount))
                .id(UUID.randomUUID().toString())
                .build();

        customer.getWallet().getTransactions().add(transaction);
    }

    @GetMapping
    public Set<Customer> getAll(){
        return customersDb;
    }
}
