package com.tradesphere.market.dto.twelvedata;

public record TwelveDataCandle(
        String datetime,
        String open,
        String high,
        String low,
        String close,
        String volume
) {
}
