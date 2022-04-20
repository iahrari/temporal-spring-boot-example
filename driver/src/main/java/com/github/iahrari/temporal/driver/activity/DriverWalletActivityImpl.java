package com.github.iahrari.temporal.driver.activity;

import com.github.iahrari.temporal.api.activity.DriverWalletActivity;
import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.api.model.Wallet;
import com.github.iahrari.temporal.driver.model.Driver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@TemporalActivity
@RequiredArgsConstructor
public class DriverWalletActivityImpl implements DriverWalletActivity {
    private final Set<Driver> driversDb;

    @Override
    public BigDecimal checkIfUserHasEnoughMoney(String userId, BigDecimal amount) {
        return driversDb.stream()
                .filter(c -> c.getDriverId().equals(userId))
                .map(c -> c.getWallet().total().subtract(amount))
                .findAny()
                .orElseThrow();
    }

    @Override
    public void exchange(String userId, Transaction transaction) {
        var customer = driversDb.stream()
                .filter(c -> c.getDriverId().equals(userId))
                .findAny().orElseThrow();

        customer.getWallet().getTransactions().add(transaction);
    }

    @Override
    public void rollback(String userId, Transaction transaction) {
        var transactions = driversDb.stream()
                .filter(c -> c.getDriverId().equals(userId))
                .findAny()
                .map(Driver::getWallet)
                .map(Wallet::getTransactions)
                .orElseThrow();

        transactions.stream().filter(t -> t.getId().equals(transaction.getId()))
                .findAny()
                .ifPresent(transactions::remove);
    }
}
