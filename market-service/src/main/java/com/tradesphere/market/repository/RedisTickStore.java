package com.tradesphere.market.repository;

import com.tradesphere.market.constants.CacheKeys;
import com.tradesphere.market.constants.CacheTTL;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisTickStore {
    private final StringRedisTemplate stringRedisTemplate;

    public Optional<String> getSearchResults(String query) {
        String key = CacheKeys.SEARCH_PREFIX + query;
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void saveSearchResults(String query, String results) {
        String key = CacheKeys.SEARCH_PREFIX + query;
        stringRedisTemplate.opsForValue().set(key, results, CacheTTL.SEARCH);
    }

    public Optional<String> getQuote(String query) {
        String key = CacheKeys.PRICE_PREFIX + query;
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void saveQuote(String query, String value) {
        String key = CacheKeys.PRICE_PREFIX + query;
        stringRedisTemplate.opsForValue().set(key, value, CacheTTL.QUOTE);
    }

    public Optional<String> getCandles(String symbol, String exchange, String interval) {
        String key = CacheKeys.CANDLES_PREFIX + symbol + ":" + exchange + ":" + interval;
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void saveCandles(String symbol, String exchange, String interval, String candlesJson) {
        String key = CacheKeys.CANDLES_PREFIX + symbol + ":" + exchange + ":" + interval;
        stringRedisTemplate.opsForValue().set(key, candlesJson, CacheTTL.CANDLES);
    }

}
