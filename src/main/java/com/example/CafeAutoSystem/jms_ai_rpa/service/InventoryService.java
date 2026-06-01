package com.example.CafeAutoSystem.jms_ai_rpa.service;

import com.example.CafeAutoSystem.jms_ai_rpa.dto.InventoryResponse;
import com.example.CafeAutoSystem.jms_ai_rpa.entity.IngredientEntity;
import com.example.CafeAutoSystem.jms_ai_rpa.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.jms_ai_rpa.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;
    private final CurrentStockLogRepository currentStockLogRepository;

    /**
     * 전체 재료 목록 + 현재 재고(LOG SUM) + 상태 계산
     */
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryList() {
        List<IngredientEntity> ingredients = ingredientRepository.findAll();

        return ingredients.stream().map(ingredient -> {
            // CURRENT_STOCK_LOG SUM으로 현재 재고 계산
            int currentStock = currentStockLogRepository
                    .findByIngredient_IngredientId(ingredient.getIngredientId())
                    .stream()
                    .mapToInt(log -> log.getAmount())
                    .sum();

            // 상태 계산
            String status = calcStatus(currentStock, ingredient.getSafetyStock());

            // 프로그레스바 비율 (안전재고 * 2 기준 100%)
            int maxStock = ingredient.getSafetyStock() * 2;
            int percent = maxStock > 0
                    ? Math.min((int) ((double) currentStock / maxStock * 100), 100)
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
