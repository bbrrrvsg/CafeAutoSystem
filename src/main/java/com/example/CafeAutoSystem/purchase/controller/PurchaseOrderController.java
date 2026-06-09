package com.example.CafeAutoSystem.purchase.controller;

import com.example.CafeAutoSystem.ai_rpa.service.AiSchedulerService;
import com.example.CafeAutoSystem.purchase.dto.PurchaseOrderDto;
import com.example.CafeAutoSystem.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final AiSchedulerService aiSchedulerService;

    // -----------------------------------------------------
    // [GET] /api/order/pending     — 승인 대기
    // [GET] /api/order/completed   — 승인 완료
    // [GET] /api/order/rejected    — 반려
    // -----------------------------------------------------
    @GetMapping("/pending")
    public List<PurchaseOrderDto> getPendingList() {
        return purchaseOrderService.getPendingList();
    }

    @GetMapping("/completed")
    public List<PurchaseOrderDto> getCompletedList() {
        return purchaseOrderService.getCompletedList();
    }

    @GetMapping("/rejected")
    public List<PurchaseOrderDto> getRejectedList() {
        return purchaseOrderService.getRejectedList();
    }

    // -----------------------------------------------------
    // [POST] /api/order  — 발주서 신규 생성 (status=PENDING)
    //   body: { vendorIngredientId, suggestedQty, finalQty?, expirationDate? }
    //   수량 단위는 식자재(ingredient.unit) 기준
    // -----------------------------------------------------
    @PostMapping
    public PurchaseOrderDto create(@RequestBody PurchaseOrderDto dto) {
        return purchaseOrderService.createOrder(dto);
    }

    // [GET] /api/order/{id} — 단건 조회
    @GetMapping("/{id}")
    public PurchaseOrderDto getOne(@PathVariable Integer id) {
        return purchaseOrderService.getById(id);
    }

    // [DELETE] /api/order/{id} — 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        purchaseOrderService.deleteOrder(id);
    }

    // -----------------------------------------------------
    // [PUT] /api/order/{id}?password=xxxx
    //   발주서 수정 (수량 / 거래처) — 점장 비밀번호 필요
    // -----------------------------------------------------
    @PutMapping("/{id}")
    public PurchaseOrderDto updateOrder(@PathVariable Integer id,
                                        @RequestParam String password,
                                        @RequestBody PurchaseOrderDto dto) {
        return purchaseOrderService.updateOrder(id, dto, password);
    }

    // -----------------------------------------------------
    // [PUT] /api/order/{id}/approve?password=xxxx
    //   점장 최종 승인 (status → COMPLETED)
    // -----------------------------------------------------
    @PutMapping("/{id}/approve")
    public PurchaseOrderDto approve(@PathVariable Integer id,
                                    @RequestParam String password) {
        return purchaseOrderService.approve(id, password);
    }

    // -----------------------------------------------------
    // [PUT] /api/order/{id}/reject?password=xxxx
    //   점장 반려 (status → REJECTED, finalQty → 0)
    // -----------------------------------------------------
    @PutMapping("/{id}/reject")
    public PurchaseOrderDto reject(@PathVariable Integer id,
                                   @RequestParam String password) {
        return purchaseOrderService.reject(id, password);
    }

    // URL: POST /api/order/bulk-ai
    @PostMapping("/bulk-ai")
    public ResponseEntity<Map<String, Object>> createBulkOrdersFromAi(@RequestBody List<PurchaseOrderDto> dtoList) {

        // 1. 서비스 단의 일괄 저장 로직 호출 (우선순위 1순위 거래처 매핑 가동)
        purchaseOrderService.createBulkOrdersFromAi(dtoList);

        // 2. 리액트 프론트엔드가 성공을 인지하고 화면을 갱신할 수 있도록 응답 맵 구성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "AI 추천 데이터 기반 발주 초안(PENDING)이 정상적으로 일괄 적재되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reanalyze")
    public ResponseEntity<Map<String, Object>> reanalyzeAiModel() {
        aiSchedulerService.runDailyAiStockAnalysis();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "PyTorch LSTM 모델이 최신 재고 로그를 기반으로 실시간 예측 분석 및 데이터 갱신을 완료했습니다.");

        return ResponseEntity.ok(response);
    }
}
