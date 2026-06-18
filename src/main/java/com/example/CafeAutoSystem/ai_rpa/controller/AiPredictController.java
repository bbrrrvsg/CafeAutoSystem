package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.service.AiPredictService;
import com.example.CafeAutoSystem.ai_rpa.service.AiSchedulerService;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/jms-ai")
@RequiredArgsConstructor
public class AiPredictController {
    private final AiPredictService aiPredictService;
    private final AiSchedulerService aiSchedulerService;

    @PostMapping("/reanalyze")
    public ResponseEntity<?> reanalyzeAiStock() {
        try {
            log.info("🎯 [API 트리거] 사장님 화면 요청으로 실시간 AI 재분석 파이프라인 가동");

            // 🌟 [정합성 방어 락 추가]: 새로운 AI 분석 수량을 장부에 적재하기 전에,
            // 오늘 날짜로 이미 생성되어 있던 기존 PENDING(대기) 발주서들이 있다면 완전히 초기화(Delete)합니다.
            // 이 처리가 없으면 재분석 버튼을 누를 때마다 발주 원장에 데이터가 똑같은 품목으로 계속 중복 누적됩니다.
            aiPredictService.clearExistingTodayPendingOrders();
            log.info("🔹 [원장 초기화] 금일 생성된 기존 AI 대기 발주 원장(PENDING) 선제적 청소 완료");

            // 메일 전송 없이 최신 파이썬 데이터를 수거해 분석 로그와 PENDING 원장을 동시 리프레시
            aiSchedulerService.runManualAiReanalysisFromWeb();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "AI 자재 수요 재분석 및 발주 대기 원장 갱신이 완료되었습니다."
            ));
        } catch (Exception e) {
            log.error("❌ 실시간 AI 재분석 처리 중 장애 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "AI 재분석 오류: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/draft")
    public ResponseEntity<PurchaseOrderEntity> createDraft(@RequestBody PurchaseOrderEntity orderEntity) {
        PurchaseOrderEntity savedOrder = aiPredictService.createAiDraftOrder(orderEntity);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrderEntity>> getOrderList() {
        List<PurchaseOrderEntity> list = aiPredictService.getAllPurchaseOrders();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/logs/{ingredientId}")
    public ResponseEntity<List<HistoricalStockLogEntity>> getHistoricalLogs(@PathVariable("ingredientId") Integer ingredientId) {
        List<HistoricalStockLogEntity> logs = aiPredictService.getHistoricalLogsByIngredient(ingredientId);
        return ResponseEntity.ok(logs);
    }
}