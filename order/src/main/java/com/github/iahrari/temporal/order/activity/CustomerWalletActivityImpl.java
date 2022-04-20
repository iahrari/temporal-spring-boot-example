package com.github.iahrari.temporal.order.activity;

import com.github.iahrari.temporal.api.activity.CustomerWalletActivity;
import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.api.model.Wallet;
import com.github.iahrari.temporal.order.model.Customer;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@TemporalActivity
@RequiredArgsConstructor
public class CustomerWalletActivityImpl implements CustomerWalletActivity {
    private final Logger logger = Workflow.getLogger(CustomerWalletActivity.class);
    private final Set<Customer> customersDb;

    @Override
    public void notifyCustomerToChargeWallet(String customerId, BigDecimal amount) {
        logger.info("You need to charge your account. customer: {}, amount: {}", customerId, amount);
    }

    @Override
    public BigDecimal checkIfUserHasEnoughMoney(String userId, BigDecimal amount) {
        return customersDb.stream()
                .filter(c -> c.getCustomerId().equals(userId))
                .map(c -> c.getWallet().total().subtract(amount))
                .findAny()
                .orElseThrow();
    }

    @Override
    public void exchange(String userId, Transaction transaction) {
        var customer = customersDb.stream()
                .filter(c -> c.getCustomerId().equals(userId))
                .findAny().orElseThrow();

        customer.getWallet().getTransactions().add(transaction);
    }

    @Override
    public void rollback(String userId, Transaction transaction) {
        var transactions = customersDb.stream()
                .filter(c -> c.getCustomerId().equals(userId))
                .findAny()
                .map(Customer::getWallet)
                .map(Wallet::getTransactions)
                .orElseThrow();

        transactions.stream().filter(t -> t.getId().equals(transaction.getId()))
                .findAny()
                .ifPresent(transactions::remove);
    }
}
