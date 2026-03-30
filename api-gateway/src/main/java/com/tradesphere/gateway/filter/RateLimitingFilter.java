package com.tradesphere.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RateLimitingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain chain){
        String IpAddress = serverWebExchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return chain.filter(serverWebExchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }

}
