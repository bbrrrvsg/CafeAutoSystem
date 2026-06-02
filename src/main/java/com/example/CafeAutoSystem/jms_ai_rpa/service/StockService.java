package com.example.CafeAutoSystem.jms_ai_rpa.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.MenuRecipeRepository;
import com.example.CafeAutoSystem.jms_ai_rpa.dto.OrderRequest;
import com.example.CafeAutoSystem.jms_ai_rpa.dto.StockOutResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final MenuRecipeRepository menuRecipeRepository;
    private final CurrentStockLogRepository currentStockLogRepository;

    /**
     * 주문 수신 → MENU_RECIPE 기반 소요량 계산 → CURRENT_STOCK_LOG STOCK_OUT 기록
     */
    @Transactional
    public StockOutResult processOrder(OrderRequest request) {
        log.info("[주문 수신] menu={} qty={}", request.getMenuName(), request.getQuantity());

        // 1. 메뉴 레시피(재료 목록) 조회
        List<MenuRecipeEntity> recipes = menuRecipeRepository
                .findByMenuNameWithIngredient(request.getMenuName());

        if (recipes.isEmpty()) {
            throw new IllegalArgumentException("레시피를 찾을 수 없습니다: " + request.getMenuName());
        }

        List<StockOutResult.StockDetail> details = new ArrayList<>();

        // 2. 재료별 소요량 계산 및 STOCK_OUT 로그 기록
        for (MenuRecipeEntity recipe : recipes) {
            IngredientEntity ingredient = recipe.getIngredient();

            // 총 소요량 = required_quantity × 주문 수량
            int totalQty = recipe.calcTotalQty(request.getQuantity());

            String message = String.format("[판매] %s %d잔 판매",
                    request.getMenuName(), request.getQuantity());

            // 3. CURRENT_STOCK_LOG INSERT (amount는 출고이므로 음수)
            CurrentStockLogEntity stockLog = CurrentStockLogEntity.builder()
                    .ingredient(ingredient)
                    .orderItemId(null)
                    .logType("STOCK_OUT")
                    .message(message)
                    .amount(-totalQty)
                    .reason("레시피 자동 차감")
                    .userId("SYSTEM")
                    .build();

            currentStockLogRepository.save(stockLog);

            log.info("[STOCK_OUT] 재료={} 차감량={}{}",
                    ingredient.getIngredientName(), totalQty, ingredient.getUnit());

            // 4. 현재 재고 합산으로 안전재고 이하 여부 확인
            int currentStock = currentStockLogRepository
                    .findByIngredient_IngredientId(ingredient.getIngredientId())
                    .stream()
                    .mapToInt(CurrentStockLogEntity::getAmount)
                    .sum();
            boolean isLowStock = currentStock <= ingredient.getSafetyStock();

            if (isLowStock) {
                log.warn("[재고부족] {} 현재고 {}{}이(가) 안전재고 {}{}에 도달",
                        ingredient.getIngredientName(), currentStock, ingredient.getUnit(),
                        ingredient.getSafetyStock(), ingredient.getUnit());

                // STOCK_WARNING 로그 추가 기록
                CurrentStockLogEntity warningLog = CurrentStockLogEntity.builder()
                        .ingredient(ingredient)
                        .orderItemId(null)
                        .logType("STOCK_WARNING")
                        .message(String.format("[재고부족] '%s'의 현재고가 안전재고 미만입니다.",
                                ingredient.getIngredientName()))
                        .amount(0)
                        .reason(String.format("현재고가 안전재고 %d%s 미만",
                                ingredient.getSafetyStock(), ingredient.getUnit()))
                        .userId("SYSTEM")
                        .build();

                currentStockLogRepository.save(warningLog);
            }

            details.add(StockOutResult.StockDetail.builder()
                    .ingredientName(ingredient.getIngredientName())
                    .unit(ingredient.getUnit())
                    .stockUsed(totalQty)
                    .currentStock(currentStock)
                    .lowStock(isLowStock)
                    .build());
        }

        return StockOutResult.builder()
                .menuName(request.getMenuName())
                .quantity(request.getQuantity())
                .details(details)
                .build();
    }

    /** 재료 ID로 출고 이력 조회 */
    @Transactional(readOnly = true)
    public List<CurrentStockLogEntity> getStockLogsByIngredient(Integer ingredientId) {
        return currentStockLogRepository.findByIngredient_IngredientId(ingredientId);
    }
}