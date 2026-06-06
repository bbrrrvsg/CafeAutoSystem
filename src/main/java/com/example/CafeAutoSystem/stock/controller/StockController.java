package com.example.CafeAutoSystem.stock.controller;

import com.example.CafeAutoSystem.ai_rpa.service.StockMigrationService;
import com.example.CafeAutoSystem.stock.dto.OrderRequest;
import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.stock.dto.StockLogView;
import com.example.CafeAutoSystem.stock.dto.StockOutResult;
import com.example.CafeAutoSystem.stock.service.StockService;
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
    private final StockMigrationService stockMigrationService;

    /**
     * [POST] 월말 마감 수동 실행 (백업 → 집계 → 삭제 → 이월).
     * 현재 장부(current)가 이력(historical)으로 백업되고, 식자재별 잔량만 STOCK_FORWARD로 이월된다.
     */
    @PostMapping("/close")
    public ResponseEntity<?> runMonthlyClose() {
        try {
            stockMigrationService.backup();
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

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

    /**
     * [GET] 전체 재고 로그 최신순 — 현재+이력 통합 (활동 로그 화면용)
     */
    @GetMapping("/logs")
    public ResponseEntity<List<StockLogView>> getAllLogs() {
        return ResponseEntity.ok(stockService.getAllLogs());
    }
}
