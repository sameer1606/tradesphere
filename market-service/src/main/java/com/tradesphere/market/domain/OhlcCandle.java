package com.tradesphere.market.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
@Data
@Document(collection = "ohlc_candles")
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(
        name = "symbol_interval_timestamp_idx",
        def = "{'symbol': 1, 'exchange': 1, 'interval': 1, 'timestamp': 1}",
        unique = true
)
public class OhlcCandle {
    @Id
    String id;
    String symbol;
    String exchange;
    String interval;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    Long volume;
    Instant timestamp;
}

