package com.example.CafeAutoSystem.stock.service;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.stock.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;
    private final CurrentStockLogRepository currentStockLogRepository;

    /**
     * 전체 재료 목록 + 현재 재고(LOG SUM) + 상태 계산
     * 쿼리 2번으로 처리: 재료 전체 조회 + 재고 SUM 일괄 집계
     */
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryList() {
        List<IngredientEntity> ingredients = ingredientRepository.findAll();

        // 재료별 재고 SUM을 한 번에 조회 (GROUP BY 쿼리 1번)
        Map<Integer, Integer> stockMap = currentStockLogRepository.HisSum().stream()
                .collect(Collectors.toMap(
                        CurrentStockLogRepository.StockSumProjection::getIngredientId,
                        p -> p.getTotalAmount() != null ? p.getTotalAmount() : 0
                ));

        return ingredients.stream().map(ingredient -> {
            int currentStock = stockMap.getOrDefault(ingredient.getIngredientId(), 0);

            String status = calcStatus(currentStock, ingredient.getSafetyStock());

            int safetyStockVal = ingredient.getSafetyStock();
            int percent = safetyStockVal > 0
                    ? Math.min((int) ((double) currentStock / safetyStockVal * 100), 100)
                    : 0;

            return InventoryResponse.builder()
                    .ingredientId(ingredient.getIngredientId())
                    .ingredientName(ingredient.getIngredientName())
                    .unit(ingredient.getUnit())
                    .currentStock(currentStock)
                    .safetyStock(ingredient.getSafetyStock())
                    .ingredientImage(ingredient.getIngredientImage())
                    .status(status)
                    .stockPercent(percent)
                    .build();
        }).collect(Collectors.toList());
    }

    private String calcStatus(int currentStock, int safetyStock) {
        if (currentStock <= safetyStock) return "LOW";
        if (currentStock <= safetyStock * 1.5) return "WARN";
        return "OK";
    }
}
