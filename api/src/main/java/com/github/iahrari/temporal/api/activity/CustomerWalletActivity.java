package com.github.iahrari.temporal.api.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.math.BigDecimal;

@ActivityInterface
public interface CustomerWalletActivity extends WalletActivity{
    @ActivityMethod
    void notifyCustomerToChargeWallet(String customerId, BigDecimal amount);
}
