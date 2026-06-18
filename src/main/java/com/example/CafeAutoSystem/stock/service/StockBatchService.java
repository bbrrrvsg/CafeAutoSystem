package com.example.CafeAutoSystem.stock.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.StockBatchEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.StockBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * FEFO(First Expired, First Out) 배치 재고 서비스
 *
 * 판매 차감 시: 유통기한이 가장 임박한 배치부터 remaining_qty 차감
 * 폐기 시: 만료된 배치 remaining_qty → 0, STOCK_DISCARD 로그 기록
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockBatchService {

    private final StockBatchRepository stockBatchRepository;
    private final CurrentStockLogRepository currentStockLogRepository;

    /**
     * 배치 생성 (발주 승인 시 호출)
     */
    public StockBatchEntity createBatch(IngredientEntity ingredient,
                                        Integer orderItemId,
                                        int qty,
                                        LocalDate expiryDate) {
        StockBatchEntity batch = StockBatchEntity.builder()
                .ingredient(ingredient)
                .orderItemId(orderItemId)
                .receivedQty(qty)
                .remainingQty(qty)
                .expiryDate(expiryDate)
                .build();
        StockBatchEntity saved = stockBatchRepository.save(batch);
        log.info("[BATCH 생성] 재료={} 수량={} 유통기한={} batchId={}",
                ingredient.getIngredientName(), qty, expiryDate, saved.getBatchId());
        return saved;
    }

    /**
     * FEFO 차감 (판매 시 호출)
     *
     * 유통기한 오름차순(임박한 것 먼저)으로 배치 순회하며 totalQty 차감.
     * 배치 재고가 부족하면 IllegalStateException 발생.
     */
    @Transactional
    public void deductFefo(IngredientEntity ingredient, int totalQty) {
        List<StockBatchEntity> batches = stockBatchRepository
                .findByIngredient_IngredientIdAndRemainingQtyGreaterThanOrderByExpiryDateAsc(
                        ingredient.getIngredientId(), 0);

        int remaining = totalQty;

        for (StockBatchEntity batch : batches) {
            if (remaining <= 0) break;

            int deduct = Math.min(batch.getRemainingQty(), remaining);
            batch.setRemainingQty(batch.getRemainingQty() - deduct);
            stockBatchRepository.save(batch);
            remaining -= deduct;

            log.info("[FEFO 차감] batchId={} 유통기한={} 차감={} 잔여={}",
                    batch.getBatchId(), batch.getExpiryDate(), deduct, batch.getRemainingQty());
        }

        if (remaining > 0) {
            throw new IllegalStateException(
                    String.format("[%s] 배치 재고 부족. 미차감 수량: %d%s",
                            ingredient.getIngredientName(), remaining, ingredient.getUnit()));
        }
    }

    /**
     * 만료 배치 자동 폐기 (스케줄러 23:00 호출)
     *
     * expiryDate <= 오늘 이고 remaining_qty > 0 인 배치를 전부 폐기.
     * remaining_qty → 0 처리 + STOCK_DISCARD 로그 기록.
     */
    @Transactional
    public void discardExpiredBatches() {
        LocalDate today = LocalDate.now();
        List<StockBatchEntity> expired = stockBatchRepository.findExpiredBatches(today);

        if (expired.isEmpty()) {
            log.info("[FEFO 폐기] 만료된 배치 없음");
            return;
        }

        for (StockBatchEntity batch : expired) {
            IngredientEntity ingredient = batch.getIngredient();
            int discardQty = batch.getRemainingQty();

            // 배치 소진
            batch.setRemainingQty(0);
            stockBatchRepository.save(batch);

            // STOCK_DISCARD 로그 기록
            CurrentStockLogEntity discardLog = CurrentStockLogEntity.builder()
                    .ingredient(ingredient)
                    .orderItemId(batch.getOrderItemId())
                    .logType("STOCK_DISCARD")
                    .amount(-discardQty)
                    .reason("유통기한 만료 자동 폐기 (FEFO)")
                    .message(String.format("[자동폐기] %s %d%s 유통기한 %s 만료",
                            ingredient.getIngredientName(), discardQty,
                            ingredient.getUnit(), batch.getExpiryDate()))
                    .userId("SYSTEM")
                    .build();
            currentStockLogRepository.save(discardLog);

            log.warn("[FEFO 폐기] batchId={} 재료={} 폐기수량={}{} 만료일={}",
                    batch.getBatchId(), ingredient.getIngredientName(),
                    discardQty, ingredient.getUnit(), batch.getExpiryDate());
        }

        log.info("[FEFO 폐기 완료] 총 {}건 배치 폐기", expired.size());
    }

    /**
     * 재료별 배치 기반 현재고 조회
     */
    @Transactional(readOnly = true)
    public int getCurrentStockFromBatch(Integer ingredientId) {
        return stockBatchRepository.sumRemainingByIngredientId(ingredientId);
    }
}
