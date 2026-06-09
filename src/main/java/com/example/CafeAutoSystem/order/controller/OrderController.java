package com.example.CafeAutoSystem.order.controller;

import com.example.CafeAutoSystem.order.dto.OrderResponseDto;
import com.example.CafeAutoSystem.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}