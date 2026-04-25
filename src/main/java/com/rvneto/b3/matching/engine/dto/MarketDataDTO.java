package com.rvneto.b3.matching.engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MarketDataDTO {

    @JsonProperty("symbol")
    private String ticker;
    private BigDecimal regularMarketPrice;
    private Double regularMarketChangePercent;
    private LocalDateTime regularMarketTime;

}