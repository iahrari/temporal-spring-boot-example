package com.github.iahrari.temporal.api.workflow;

import com.github.iahrari.temporal.api.model.Order;
import com.github.iahrari.temporal.api.model.OrderStatus;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod
    void init(Order order);

    @SignalMethod
    void customerPaidTheBill();

    @SignalMethod
    void driverAccepted(String requestId, String driverId);

    @SignalMethod
    void updateStatus(String requestId, OrderStatus status);

    @QueryMethod
    Order query();

    @QueryMethod
    boolean queryException(String id);
}
