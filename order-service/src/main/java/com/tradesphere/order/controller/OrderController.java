package com.tradesphere.order.controller;

import com.tradesphere.order.dto.PlaceOrderRequest;
import com.tradesphere.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity placeOrder(@Valid @RequestBody PlaceOrderRequest request){
        orderService.placeOrder(request);
        return ResponseEntity.ok("Order placed successfully-in Order Service.");
    }
    @PatchMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully-in Order Service.");
    }
}

