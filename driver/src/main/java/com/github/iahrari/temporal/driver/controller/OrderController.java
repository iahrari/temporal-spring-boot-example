package com.github.iahrari.temporal.driver.controller;

import com.github.iahrari.temporal.api.model.OrderStatus;
import com.github.iahrari.temporal.api.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/v1/order/{orderId}")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final WorkflowClient workflowClient;

    @PutMapping("{driverId}/accept")
    public ResponseEntity<Void> acceptOrder(@PathVariable String driverId, @PathVariable String orderId) {
        var flow = workflowClient.newWorkflowStub(OrderWorkflow.class, orderId);
        var requestId = UUID.randomUUID().toString();
        flow.driverAccepted(requestId, driverId);

        if (flow.queryException(requestId))
            return ResponseEntity.badRequest().build();
        else return ResponseEntity.ok().build();
    }

    @PutMapping("/pickup")
    public ResponseEntity<Void> pickup(@PathVariable String orderId){
        var flow = workflowClient.newWorkflowStub(OrderWorkflow.class, orderId);
        var requestId = UUID.randomUUID().toString();
        flow.updateStatus(requestId, OrderStatus.PICKUP);

        if (flow.queryException(requestId))
            return ResponseEntity.badRequest().build();
        else return ResponseEntity.ok().build();
    }

    @PutMapping("/delivered")
    public ResponseEntity<Void> delivered(@PathVariable String orderId){
        var flow = workflowClient.newWorkflowStub(OrderWorkflow.class, orderId);
        var requestId = UUID.randomUUID().toString();
        flow.updateStatus(requestId, OrderStatus.DELIVERED);
        if (flow.queryException(requestId))
            return ResponseEntity.badRequest().build();
        else return ResponseEntity.ok().build();
    }
}