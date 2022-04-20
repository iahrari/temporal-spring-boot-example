package com.github.iahrari.temporal.order.workflow;

import com.github.iahrari.temporal.api.Shared;
import com.github.iahrari.temporal.api.activity.*;
import com.github.iahrari.temporal.api.annotations.TemporalWorkflow;
import com.github.iahrari.temporal.api.model.Order;
import com.github.iahrari.temporal.api.model.OrderStatus;
import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.api.workflow.OrderWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@TemporalWorkflow
public class OrderWorkflowImpl implements OrderWorkflow {
    private final Map<String, Boolean> exceptions = new HashMap<>();
    private boolean customerWalletUpdated = false;
    private Order flowState;

    private final Function<String, ActivityOptions> optFun = (taskQueue) -> ActivityOptions.newBuilder()
            .setTaskQueue(taskQueue)
            .setStartToCloseTimeout(Duration.ofSeconds(2))
            .setRetryOptions(
                    RetryOptions.newBuilder()
                            .setBackoffCoefficient(2)
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumAttempts(5)
                            .build()
            ).build();

    private final CalculateOrderPriceActivity priceActivity = Workflow.newActivityStub(
            CalculateOrderPriceActivity.class,
            optFun.apply(Shared.ORDER_TASK_QUEUE)
    );

    private final CustomerWalletActivity customerWalletActivity = Workflow.newActivityStub(
            CustomerWalletActivity.class,
            optFun.apply(Shared.ORDER_TASK_QUEUE)
    );

    private final NotifyCustomerOrderStatusUpdate notifyCustomer = Workflow.newActivityStub(
            NotifyCustomerOrderStatusUpdate.class,
            optFun.apply(Shared.ORDER_TASK_QUEUE)
    );

    private final DriverWalletActivity driverWalletActivity = Workflow.newActivityStub(
            DriverWalletActivity.class,
            optFun.apply(Shared.DRIVER_TASK_QUEUE)
    );

    private final SendDriverOrderNotify sendDriverOrderNotify = Workflow.newActivityStub(
            SendDriverOrderNotify.class,
            optFun.apply(Shared.DRIVER_TASK_QUEUE)
    );

    private final FindDriverActivity findDriverActivity = Workflow.newActivityStub(
            FindDriverActivity.class,
            optFun.apply(Shared.ALLOCATION_TASK_QUEUE)
    );

    private final Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).build();
    private final Saga saga = new Saga(sagaOptions);

    @Override
    public void init(Order order) {
        flowState = order;
        try {
            var amount = priceActivity.calculatePrice(flowState.getLat(), flowState.getLng());
            followStateToPaid(amount);

            pickDriver(amount);

            Workflow.await(() -> flowState.getStatus() == OrderStatus.PICKUP || flowState.getStatus() == OrderStatus.CANCELED);
            if (flowState.getStatus() != OrderStatus.CANCELED) {
                notifyCustomer.notifyCustomer(flowState);
                Workflow.await(() -> flowState.getStatus() == OrderStatus.DELIVERED);
                notifyCustomer.notifyCustomer(flowState);
            } else {
                saga.compensate();
            }
        } catch (ActivityFailure e) {
            saga.compensate();
            flowState.setStatus(OrderStatus.FAILED);
        }
    }

    private void pickDriver(BigDecimal amount) {
        var isDriverPicked = false;
        while (!isDriverPicked){
            try {
                allocateOrderToDriver(amount);
                isDriverPicked = true;
            } catch (ActivityFailure e){
                flowState.setStatus(OrderStatus.PAID);
                flowState.setDriverId(null);
                e.printStackTrace();
            }
        }
    }

    private void allocateOrderToDriver(BigDecimal amount) {
        while (flowState.getStatus() != OrderStatus.ACCEPTED && flowState.getStatus() != OrderStatus.CANCELED) {
            var driverId = findDriverActivity.findDriver(flowState.getLat(), flowState.getLng());
            if (driverId != null) sendDriverOrderNotify.send(flowState, driverId);
            Workflow.await(Duration.ofSeconds(30),
                    () -> flowState.getStatus() == OrderStatus.ACCEPTED
                            || flowState.getStatus() == OrderStatus.CANCELED);
        }
        if (flowState.getStatus() != OrderStatus.CANCELED) {
            var driverTransaction = Workflow.sideEffect(Transaction.class,
                    () -> Transaction.builder()
                            .status(Transaction.TransactionStatus.ADDITION)
                            .id(UUID.randomUUID().toString())
                            .amount(amount)
                            .build()
            );
            driverWalletActivity.exchange(flowState.getDriverId(), driverTransaction);
            saga.addCompensation(driverWalletActivity::rollback, flowState.getDriverId(), driverTransaction);
            notifyCustomer.notifyCustomer(flowState);
        }
    }

    private void followStateToPaid(BigDecimal amount) {
        var hasEnoughMoney = BigDecimal.valueOf(-1);
        while (hasEnoughMoney.compareTo(BigDecimal.ZERO) < 0 && flowState.getStatus() != OrderStatus.CANCELED) {
            hasEnoughMoney = customerWalletActivity.checkIfUserHasEnoughMoney(flowState.getCustomerId(), amount);
            BigDecimal finalHasEnoughMoney = hasEnoughMoney;
            var isUpdated = Workflow.await(Duration.ofSeconds(60),
                    () -> finalHasEnoughMoney.compareTo(BigDecimal.ZERO) > 0 || customerWalletUpdated || flowState.getStatus() == OrderStatus.CANCELED);
            if (!isUpdated && flowState.getStatus() != OrderStatus.CANCELED)
                customerWalletActivity.notifyCustomerToChargeWallet(flowState.getCustomerId(), amount);
        }

        Transaction customerTransaction = Workflow.sideEffect(Transaction.class,
                () -> Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .amount(amount)
                        .status(Transaction.TransactionStatus.DEDUCTION)
                        .build()
        );

        if (flowState.getStatus() != OrderStatus.CANCELED) {
            customerWalletActivity.exchange(flowState.getCustomerId(), customerTransaction);
            saga.addCompensation(customerWalletActivity::rollback, flowState.getCustomerId(), customerTransaction);

            flowState.setStatus(OrderStatus.PAID);
            notifyCustomer.notifyCustomer(flowState);
        }
    }

    @Override
    public void customerPaidTheBill() {
        customerWalletUpdated = true;
    }

    @Override
    public void driverAccepted(String requestId, String driverId) {
        var problem = false;
        if (flowState.getStatus().ordinal() < OrderStatus.ACCEPTED.ordinal()) {
            flowState.setStatus(OrderStatus.ACCEPTED);
            flowState.setDriverId(driverId);
        } else {
            problem = true;
        }
        exceptions.put(requestId, problem);
    }

    @Override
    public void updateStatus(String requestId, OrderStatus status) {
        var problem = false;
        if (status == OrderStatus.CANCELED){
            if (flowState.getStatus().ordinal() < OrderStatus.PICKUP.ordinal())
                flowState.setStatus(OrderStatus.CANCELED);
            else problem = true;
        } else {
            if (status.ordinal() - flowState.getStatus().ordinal() == 1)
                flowState.setStatus(status);
            else problem = true;
        }
        exceptions.put(requestId, problem);
    }

    @Override
    public Order query() {
        return flowState;
    }

    @Override
    public boolean queryException(String id) {
        return exceptions.getOrDefault(id, false);
    }
}
