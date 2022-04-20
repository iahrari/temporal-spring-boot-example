package com.github.iahrari.temporal.api.activity;

import com.github.iahrari.temporal.api.model.Transaction;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.math.BigDecimal;

@ActivityInterface
public interface WalletActivity {
    @ActivityMethod
    BigDecimal checkIfUserHasEnoughMoney(String userId, BigDecimal amount);

    @ActivityMethod
    void exchange(String userId, Transaction transaction);

    @ActivityMethod
    void rollback(String userId, Transaction transaction);
}
