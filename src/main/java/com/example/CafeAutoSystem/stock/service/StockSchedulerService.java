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

    @Transactional
    public void discardExpiredIngredients() {
        LocalDateTime now = LocalDateTime.now();

        List<CurrentStockLogEntity> expiredStocks = currentStockLogRepository.findExpiredStocks(now);

        if (expiredStocks.isEmpty()) {
            log.info("만료된 자재가 없습니다.");
            return;
        }

        for (CurrentStockLogEntity stockBatch : expiredStocks) {

            // ingredient 객체에서 직접 꺼내서 비관적 락 획득
            IngredientEntity ingredient = ingredientRepository
                    .findByIdForUpdate(stockBatch.getIngredient().getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "존재하지 않는 자재 ID: " + stockBatch.getIngredient().getIngredientId()));

            int discardAmount = stockBatch.getAmount();
            if (discardAmount <= 0) continue;

            // HISTORICAL_STOCK_LOG에 영구 폐기 기록
            HistoricalStockLogEntity discardLog = HistoricalStockLogEntity.builder()
                    .ingredientId(ingredient.getIngredientId())
                    .logType("STOCK_DISCARD")
                    .message(String.format("[자동폐기] 유통기한 경과 %s %d%s 폐기 처리",
                            ingredient.getIngredientName(), discardAmount, ingredient.getUnit()))
                    .amount(-discardAmount)
                    .reason("EXPIRED")
                    .userId("SYSTEM")
                    .build();
            historicalStockLogRepository.save(discardLog);

            // CURRENT_STOCK_LOG 해당 배치 amount → 0
            stockBatch.setAmount(0);
            currentStockLogRepository.save(stockBatch);

            log.info("[자동폐기 완료] 자재={} 폐기수량={}{}",
                    ingredient.getIngredientName(), discardAmount, ingredient.getUnit());
        }
    }
}