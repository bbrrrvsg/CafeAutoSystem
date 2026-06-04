package com.example.CafeAutoSystem.jms_ai_rpa.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
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
    private final IngredientRepository ingredientRepository;

    @Transactional
    public StockOutResult processOrder(OrderRequest request) {
        log.info("[주문 수신] menu={} qty={}", request.getMenuName(), request.getQuantity());

        List<MenuRecipeEntity> recipes = menuRecipeRepository
                .findByMenuNameWithIngredient(request.getMenuName());

        if (recipes.isEmpty()) {
            throw new IllegalArgumentException("레시피를 찾을 수 없습니다: " + request.getMenuName());
        }

        List<StockOutResult.StockDetail> details = new ArrayList<>();

        for (MenuRecipeEntity recipe : recipes) {
            IngredientEntity ingredient = recipe.getIngredient();
            int totalQty = recipe.calcTotalQty(request.getQuantity());

            String message = String.format("[판매] %s %d잔 판매",
                    request.getMenuName(), request.getQuantity());

            // STOCK_OUT 로그 INSERT
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

            // 현재 재고 SUM 계산
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

    /** 실시간 전산 오차 모니터링 및 마이너스 재고 방지 차감 */
    @Transactional
    public void decreaseStockSecure(Integer ingredientId, int amount) {
        IngredientEntity ingredient = ingredientRepository.findByIdForUpdate(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자재입니다. ID: " + ingredientId));

        int realTimeCurrentStock = currentStockLogRepository.convertToCurrentStock(ingredientId);

        if (realTimeCurrentStock - amount < 0) {
            throw new IllegalStateException(
                    String.format("[%s] 재고가 부족합니다. 현재 재고: %d, 차감 요청: %d",
                            ingredient.getIngredientName(), realTimeCurrentStock, amount));
        }

        CurrentStockLogEntity reductionLog = CurrentStockLogEntity.builder()
                .ingredient(ingredient)
                .logType("STOCK_OUT")
                .message(String.format("[차감] 정상 영업 소모 -%d%s", amount, ingredient.getUnit()))
                .amount(-amount)
                .reason("DAILY_CONSUME")
                .userId("SYSTEM")
                .build();

        currentStockLogRepository.save(reductionLog);
    }
}