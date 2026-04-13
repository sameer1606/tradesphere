package com.tradesphere.market.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


public record FinnhubQuoteResponse(
        @JsonProperty("h") BigDecimal high,
        @JsonProperty("l") BigDecimal low,
        @JsonProperty("o") BigDecimal open,
        @JsonProperty("pc") BigDecimal previousClose,
        @JsonProperty("c") BigDecimal currentPrice,
        @JsonProperty("t") long timestamp
        ) {
}
