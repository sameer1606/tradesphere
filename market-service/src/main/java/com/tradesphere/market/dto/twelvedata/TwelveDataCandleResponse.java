package com.tradesphere.market.dto.twelvedata;

import java.util.List;

public record TwelveDataCandleResponse(
        TwelveDataMeta meta,
        List<TwelveDataCandle> values
) {
}
