package com.tradesphere.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TradeSphere — Market Service
 *
 * On startup:
 *   1. CandleSeedJob seeds OHLC_CANDLE table from Twelve Data REST API
 *   2. FinnhubWsClient opens persistent WebSocket to wss://ws.finnhub.io
 *   3. Subscribes to configured symbols
 *
 * On every tick (three-way fanout):
 *   1. Normalize raw Finnhub payload → PriceTick domain object
 *   2. RedisTickStore: SET price:{symbol} {json} EX 30
 *   3. RedisPubSubPublisher: PUBLISH price:tick:{symbol} {json}
 *   4. STOMP WebSocket: /topic/price.{symbol} → subscribed Angular clients
 *
 * Provider abstraction:
 *   MarketDataProvider (interface) ← FinnhubProvider (today)
 *                                  ← ZerodhaProvider (future, zero code change)
 *
 * Does NOT publish to Kafka.
 * price.tick is ephemeral — Redis Pub/Sub is the correct channel.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MarketServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketServiceApplication.class, args);
    }
}
