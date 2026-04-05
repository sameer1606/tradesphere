package com.tradesphere.order.controller;

import com.tradesphere.order.dto.PlaceOrderRequest;
import com.tradesphere.order.repository.OrderRepository;
import com.tradesphere.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity placeOrder(@Valid @RequestBody PlaceOrderRequest request){
        orderService.placeOrder(request);
        return ResponseEntity.ok("Order placed successfully and funds reserved.");
    }
}

