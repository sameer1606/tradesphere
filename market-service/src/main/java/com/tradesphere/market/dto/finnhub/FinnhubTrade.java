package com.tradesphere.market.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record FinnhubTrade(
            @JsonProperty("s") String symbol,
            @JsonProperty("p") BigDecimal price,
            @JsonProperty("t") long timestamp,
            @JsonProperty("v") long volume
    ) {}
