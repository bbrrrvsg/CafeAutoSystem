package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.common.repository.HistoricalStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.common.repository.PurchaseOrderRepository;
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
    private final IngredientRepository ingredientRepository;

    // [Create] AI 발주 제안 초안 데이터 단건 저장 (1차 검증용)
    public PurchaseOrderEntity createAiDraftOrder(PurchaseOrderEntity order) {
        order.setStatus("PENDING");
        return purchaseOrderRepository.save(order);
    }

    // [Read] 사장님 화면에 뿌려줄 현재 발주서 전체 목록 조회
    @Transactional(readOnly = true)
    public List<PurchaseOrderEntity> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    // [Read] 특정 식자재의 과거 누적 로그 조회 (AI 통계 가동용)
    @Transactional(readOnly = true)
    public List<HistoricalStockLogEntity> getHistoricalLogsByIngredient(Integer ingredientId) {
        return historicalStockLogRepository.findByIngredientId(ingredientId);
    }

    @Transactional
    public void saveAiAnalysisLog(Integer ingredientId, String logType, Integer amount, String message, String reason) {
        HistoricalStockLogEntity logEntity = new HistoricalStockLogEntity();
        logEntity.setIngredientId(ingredientId);
        logEntity.setLogType(logType);
        logEntity.setAmount(amount);
        logEntity.setMessage(message);
        logEntity.setReason(reason);
        logEntity.setUserId("SYSTEM");
        historicalStockLogRepository.saveAndFlush(logEntity);
    }
    @Transactional(readOnly = true)
    public int getSafetyStockByIngredient(Integer ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(IngredientEntity::getSafetyStock)
                .orElse(0); // 혹시 식자재가 없으면 방어코드로 0 반환
    }
}
