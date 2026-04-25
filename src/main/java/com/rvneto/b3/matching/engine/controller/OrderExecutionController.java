package com.rvneto.b3.matching.engine.controller;

import com.rvneto.b3.matching.engine.model.ExecutionStatus;
import com.rvneto.b3.matching.engine.model.OrderExecution;
import com.rvneto.b3.matching.engine.repository.OrderExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
public class OrderExecutionController {

    private final OrderExecutionRepository repository;

    @GetMapping
    public ResponseEntity<List<OrderExecution>> findAll() {
        log.info("Fetching all order executions");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderExecution> findByOrderId(@PathVariable String orderId) {
        log.info("Fetching execution for orderId: {}", orderId);
        return repository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<List<OrderExecution>> findByTicker(@PathVariable String ticker) {
        log.info("Fetching executions for ticker: {}", ticker);
        return ResponseEntity.ok(repository.findByTicker(ticker.toUpperCase()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderExecution>> findByStatus(@PathVariable String status) {
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