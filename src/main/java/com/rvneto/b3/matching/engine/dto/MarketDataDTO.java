package com.rvneto.b3.matching.engine.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDataDTO {

    @JsonAlias("symbol")
    private String ticker;
    private BigDecimal regularMarketPrice;
    private Double regularMarketChangePercent;
    private LocalDateTime regularMarketTime;

}
