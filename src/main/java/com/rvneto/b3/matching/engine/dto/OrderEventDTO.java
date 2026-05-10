package com.rvneto.b3.matching.engine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEventDTO {

    private String orderId;
    private String ticker;
    private Integer quantity;
    private BigDecimal price;
    private String side;

}
