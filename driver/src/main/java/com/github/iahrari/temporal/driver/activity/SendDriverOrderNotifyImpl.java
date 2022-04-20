package com.github.iahrari.temporal.driver.activity;

import com.github.iahrari.temporal.api.activity.SendDriverOrderNotify;
import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import com.github.iahrari.temporal.api.model.Order;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@TemporalActivity
public class SendDriverOrderNotifyImpl implements SendDriverOrderNotify {
    private final Logger logger = Workflow.getLogger(SendDriverOrderNotify.class);

    @Override
    public void send(Order order, String driverId) {
        logger.info("Hey driver {}, there's a possible order waiting for you: {}", driverId, order);
    }
}
