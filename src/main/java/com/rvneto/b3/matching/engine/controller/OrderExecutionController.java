package com.rvneto.b3.matching.engine.controller;

import com.rvneto.b3.matching.engine.model.ExecutionStatus;
import com.rvneto.b3.matching.engine.model.OrderExecution;
import com.rvneto.b3.matching.engine.repository.OrderExecutionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
@Tag(name = "Order Executions", description = "Endpoints for querying order execution history")
public class OrderExecutionController {

    private final OrderExecutionRepository repository;

    @GetMapping
    @Operation(summary = "List all executions", description = "Returns all order executions recorded by the matching engine")
    public ResponseEntity<List<OrderExecution>> findAll() {
        log.info("Fetching all order executions");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Find by order ID", description = "Returns the execution record for a specific broker order ID")
    public ResponseEntity<OrderExecution> findByOrderId(
            @Parameter(description = "The original broker order ID") @PathVariable String orderId) {
        log.info("Fetching execution for orderId: {}", orderId);
        return repository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ticker/{ticker}")
    @Operation(summary = "Find by ticker", description = "Returns all executions for a given stock ticker")
    public ResponseEntity<List<OrderExecution>> findByTicker(
            @Parameter(description = "Stock ticker symbol, e.g. PETR4") @PathVariable String ticker) {
        log.info("Fetching executions for ticker: {}", ticker);
        return ResponseEntity.ok(repository.findByTicker(ticker.toUpperCase()));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Find by status", description = "Returns all executions filtered by status: FILLED, REJECTED or EXPIRED")
    public ResponseEntity<List<OrderExecution>> findByStatus(
            @Parameter(description = "Execution status: FILLED, REJECTED or EXPIRED") @PathVariable String status) {
        log.info("Fetching executions with status: {}", status);
        try {
            ExecutionStatus executionStatus = ExecutionStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(repository.findByStatus(executionStatus));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status requested: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }
}
