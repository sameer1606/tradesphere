package com.tradesphere.market.controller;

import com.tradesphere.market.domain.PriceTick;
import com.tradesphere.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market/quote")
@RequiredArgsConstructor
public class QuoteController {
    private final MarketService marketService;

    @GetMapping("/{symbol}")
    public ResponseEntity<PriceTick> getPriceTick(@PathVariable String symbol) {
        return ResponseEntity.ok(marketService.getQuote(symbol));
    }
}
