package com.example.CafeAutoSystem.stock.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.HistoricalStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockSchedulerService {

    private final CurrentStockLogRepository currentStockLogRepository;
    private final HistoricalStockLogRepository historicalStockLogRepository;
    private final IngredientRepository ingredientRepository;
    private final StockBatchService stockBatchService;

    /**
     * 유통기한 만료 자재 자동 폐기 (StockDiscardScheduler → 23:00 호출)
     *
     * FEFO 도입 후: stock_batch 기반으로 만료 배치 조회 및 폐기.
     * 기존 current_stock_log JOIN 방식은 stock_batch 방식으로 대체.
     */
    @Transactional
    public void discardExpiredIngredients() {
        log.info("[폐기 스케줄러] FEFO 기반 만료 배치 폐기 시작");
        // StockBatchService가 만료 배치 조회 → remaining_qty=0 처리 → STOCK_DISCARD 로그 기록
        stockBatchService.discardExpiredBatches();
        log.info("[폐기 스케줄러] 완료");
    }
}
