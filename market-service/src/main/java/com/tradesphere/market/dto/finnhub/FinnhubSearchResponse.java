package com.tradesphere.market.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FinnhubSearchResponse(
       int count,
       @JsonProperty("result") List<FinnhubInstrument> result
) {
}
