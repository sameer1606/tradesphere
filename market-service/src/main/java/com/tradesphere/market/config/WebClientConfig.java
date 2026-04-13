package com.tradesphere.market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${finnhub.base-url}") private String finnhubBaseUrl;
    @Value("${twelvedata.base-url}") private String twelveDataBaseUrl;

    @Bean("finnhubWebClient")
    public WebClient finnhubWebClient() {
        return WebClient.builder()
                .baseUrl(finnhubBaseUrl)
                .build();
    }

    @Bean("twelveDataWebClient")
    public WebClient twelveDataWebClient() {
        return WebClient.builder()
                .baseUrl(twelveDataBaseUrl)
                .build();
    }

}
