package com.rvneto.b3.matching.engine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SideStatus side;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "executed_price", nullable = false)
    private BigDecimal executedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @CreationTimestamp
    @Column(name = "execution_time", nullable = false, updatable = false)
    private LocalDateTime executionTime;

}
