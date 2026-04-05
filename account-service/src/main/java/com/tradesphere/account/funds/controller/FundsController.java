package com.tradesphere.account.funds.controller;

import com.tradesphere.account.funds.dto.FinalizeReservationRequest;
import com.tradesphere.account.funds.dto.ReleaseReservationRequest;
import com.tradesphere.account.funds.dto.ReserveFundsRequest;
import com.tradesphere.account.funds.service.FundsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/funds")
@RequiredArgsConstructor
public class FundsController {
    private final FundsService fundsService;

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveReservation(@Valid @RequestBody ReserveFundsRequest request) {
        fundsService.reserve(request.accountProfileId(), request.orderId(), request.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseReservation(@Valid @RequestBody ReleaseReservationRequest request) {
        fundsService.release(request.orderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finalize")
    public ResponseEntity<Void> finalizeReservation(@Valid @RequestBody FinalizeReservationRequest request) {
        fundsService.finalizeReservation(request.orderId());
        return ResponseEntity.ok().build();
    }

}
