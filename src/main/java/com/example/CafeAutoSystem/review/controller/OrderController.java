package com.example.CafeAutoSystem.review.controller;

import com.example.CafeAutoSystem.review.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


//    @PostMapping
//    public ResponseEntity<?> menuOrder(){
//
//        orderService.menuOrder();
//
//    }
}
