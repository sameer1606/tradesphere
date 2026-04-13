package com.tradesphere.market.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


    public record FinnhubTradeResponse(
            @JsonProperty("type") String type,
            @JsonProperty("data") List<FinnhubTrade> data
    ) {}

