package com.github.iahrari.temporal.order.controller;

import com.github.iahrari.temporal.api.model.Order;
import com.github.iahrari.temporal.api.model.OrderStatus;
import com.github.iahrari.temporal.api.model.Transaction;
import com.github.iahrari.temporal.api.workflow.OrderWorkflow;
import com.github.iahrari.temporal.order.model.Customer;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final Set<Customer> customersDb;
    private final WorkflowClient workflowClient;
    private final WorkflowOptions workflowOptions;

    @PostMapping
    public Order createOrder(@RequestBody Order order){
        var orderFlow = workflowClient.newWorkflowStub(OrderWorkflow.class, workflowOptions);
        var workflowExecution = WorkflowClient.start(orderFlow::init, order);
        var o = orderFlow.query();
        o.setId(workflowExecution.getWorkflowId());
        return o;
    }

    @GetMapping("/{id}")
    public Order queryOrder(@PathVariable String id){
        var orderFlow = workflowClient.newWorkflowStub(OrderWorkflow.class, id);
        return orderFlow.query();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id){
        var orderFlow = workflowClient.newWorkflowStub(OrderWorkflow.class, id);
        var requestId = UUID.randomUUID().toString();
        orderFlow.updateStatus(requestId, OrderStatus.CANCELED);
        orderFlow.queryException(requestId);
        if (orderFlow.queryException(requestId))
            return ResponseEntity.badRequest().build();
        else return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/{id}/{amount}")
    public void chargeWallet(@PathVariable Long amount, @PathVariable String id, @PathVariable String orderId){
        var customer = customersDb.stream()
                .filter(c -> c.getCustomerId().equals(id))
                .findAny().orElseThrow();

        var transaction = Transaction.builder()
                .status(Transaction.TransactionStatus.ADDITION)
                .amount(BigDecimal.valueOf(amount))
                .id(UUID.randomUUID().toString())
                .build();

        customer.getWallet().getTransactions().add(transaction);
        workflowClient.newWorkflowStub(OrderWorkflow.class, orderId).customerPaidTheBill();
    }
}
