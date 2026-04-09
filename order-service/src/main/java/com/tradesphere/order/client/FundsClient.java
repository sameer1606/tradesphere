package com.tradesphere.order.client;

import com.tradesphere.order.dto.ReleaseFundsRequest;
import com.tradesphere.order.dto.ReserveFundsRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class FundsClient {
    private final WebClient accountServiceWebClient;
    public FundsClient(@Qualifier("accountServiceWebClient") WebClient accountServiceWebClient) {
        this.accountServiceWebClient = accountServiceWebClient;
    }

    public void reserveFunds(UUID accountProfileId, UUID orderId, BigDecimal amount) {
        accountServiceWebClient.post()
                .uri("/internal/funds/reserve")
                .bodyValue(new ReserveFundsRequest(accountProfileId, orderId, amount))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void releaseFunds(UUID orderId) {
        accountServiceWebClient.post()
                .uri("/internal/funds/release")
                .bodyValue(new ReleaseFundsRequest(orderId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
