package com.tradesphere.market.controller;

import com.tradesphere.market.domain.Instrument;
import com.tradesphere.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market/instruments")
@RequiredArgsConstructor
public class InstrumentController {
    private final MarketService marketService;
    @GetMapping("/search")
    public ResponseEntity<List<Instrument>> searchInstruments(@RequestParam("query") String query){
        return ResponseEntity.ok(marketService.searchInstruments(query));
    }
}
