package com.example.CafeAutoSystem.jms_ai_rpa.service;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.jms_ai_rpa.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.jms_ai_rpa.repository.HistoricalStockLogRepository;
import com.example.CafeAutoSystem.jms_ai_rpa.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AiPredictService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final HistoricalStockLogRepository historicalStockLogRepository;

    // [Create] AI 발주 제안 초안 데이터 단건 저장 기능 (1차 검증용)
    public PurchaseOrderEntity createAiDraftOrder(PurchaseOrderEntity orderEntity) {
        //수동 개입 전이므로 초기 상태는 PENDING으로 강제 세팅
        orderEntity.setStatus("PENDING");
        return purchaseOrderRepository.save(orderEntity);
    }

    // [Read] 사장님 화면에 뿌려줄 현재 발주서 전체 목록 조회 기능
    @Transactional(readOnly = true)
    public List<PurchaseOrderEntity> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    // [Read] 특정 식자재(예: 우유 ID=4)의 과거 누적 로그만 싹 긁어오기 (AI 통계 가동용)
    @Transactional(readOnly = true)
    public List<HistoricalStockLogEntity> getHistoricalLogsByIngredient(Integer ingredientId) {
        return historicalStockLogRepository.findByIngredientId(ingredientId);
    }
}
