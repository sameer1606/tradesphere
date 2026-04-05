package com.tradesphere.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, Long> redisTemplate;

    private static final int RATE_LIMIT = 10;
    private static final int WINDOW_SECONDS = 60;

    public RateLimitingFilter(ReactiveRedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        String redisKey = "rate_limit:" + ipAddress;

        String luaScript =
                "local count = redis.call('INCR', KEYS[1])\n" +
                        "if count == 1 then\n" +
                        "  redis.call('EXPIRE', KEYS[1], ARGV[1])\n" +
                        "end\n" +
                        "return count";

        return redisTemplate.execute(
                        new DefaultRedisScript<>(luaScript, Long.class),
                        Collections.singletonList(redisKey),
                        String.valueOf(WINDOW_SECONDS)
                )
                .next()
                .flatMap(count -> {
                    if (count != null && count > RATE_LIMIT) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                });
    }
}