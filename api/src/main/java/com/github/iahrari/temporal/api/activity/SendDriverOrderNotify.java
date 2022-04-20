package com.github.iahrari.temporal.api.activity;

import com.github.iahrari.temporal.api.model.Order;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SendDriverOrderNotify {
    @ActivityMethod
    void send(Order order, String driverId);
}
