package com.example.CafeAutoSystem.jms_ai_rpa.controller;

import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.jms_ai_rpa.service.AiPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/jms-ai")
@RequiredArgsConstructor
public class AiPredictController {
    private final AiPredictService aiPredictService;

    // [Create] AI 발주 제안 초안 강제 생성 API
    @PostMapping("/draft")
    public ResponseEntity<PurchaseOrderEntity> createDraft(@RequestBody PurchaseOrderEntity order) {
        PurchaseOrderEntity saved = aiPredictService.createAiDraftOrder(order);
        return ResponseEntity.ok(saved);
    }

    // [Read] 생성된 모든 발주서 리스트 확인 API
    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrderEntity>> getOrderList() {
        return ResponseEntity.ok(aiPredictService.getAllPurchaseOrders());
    }

    // [Read] AI가 분석할 특정 자재의 과거 전체 로그 조회 API
    @GetMapping("/logs/{ingredientId}")
    public ResponseEntity<List<HistoricalStockLogEntity>> getHistoricalLogs(@PathVariable("ingredientId") Integer ingredientId) {
        return ResponseEntity.ok(aiPredictService.getHistoricalLogsByIngredient(ingredientId));
    }
}
