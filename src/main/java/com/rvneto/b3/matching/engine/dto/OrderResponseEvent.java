package com.rvneto.b3.matching.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderResponseEvent {

    private String orderId;
    private String status;
    private BigDecimal executedPrice;

}