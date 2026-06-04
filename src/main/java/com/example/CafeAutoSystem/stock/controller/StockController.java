package com.example.CafeAutoSystem.stock.controller;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.jms_ai_rpa.dto.OrderRequest;
import com.example.CafeAutoSystem.jms_ai_rpa.dto.StockOutResult;
import com.example.CafeAutoSystem.jms_ai_rpa.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    /**
     * [POST] 주문 수신 → STOCK_OUT 처리
     * Body: { "menuName": "카페 라떼", "quantity": 2 }
     */
    @PostMapping("/order")
    public ResponseEntity<?> receiveOrder(@RequestBody OrderRequest request) {
        try {
            StockOutResult result = stockService.processOrder(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * [GET] 재료 ID로 CURRENT_STOCK_LOG 이력 조회
     */
    @GetMapping("/stock-log/ingredient/{ingredientId}")
    public ResponseEntity<List<CurrentStockLogEntity>> getLogsByIngredient(
            @PathVariable Integer ingredientId) {
        return ResponseEntity.ok(stockService.getStockLogsByIngredient(ingredientId));
    }
}
