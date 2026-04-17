package com.tradesphere.market.repository;

import com.tradesphere.market.domain.OhlcCandle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OhlcCandleRepository extends MongoRepository<OhlcCandle, String> {
    List<OhlcCandle> findBySymbolAndExchangeAndIntervalAndTimestampBetween(
            String symbol, String exchange, String interval, Instant from, Instant to
    );
}

