package com.rvneto.b3.matching.engine.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderEventDTO {

    private String orderId;
    private String ticker;
    private Integer quantity;
    private BigDecimal price;
    private String side;

}