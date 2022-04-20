package com.github.iahrari.temporal.order.activity;

import com.github.iahrari.temporal.api.activity.NotifyCustomerOrderStatusUpdate;
import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import com.github.iahrari.temporal.api.model.Order;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@TemporalActivity
public class NotifyCustomerOrderStatusUpdateImpl implements NotifyCustomerOrderStatusUpdate {
    private final Logger logger = Workflow.getLogger(NotifyCustomerOrderStatusUpdate.class);
    @Override
    public void notifyCustomer(Order order) {
        logger.info("Order status updated: {}", order);
    }
}
