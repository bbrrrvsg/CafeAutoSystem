package com.example.CafeAutoSystem.stock.service;

import com.example.CafeAutoSystem.ai_rpa.service.SseEmitterManager;
import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.HistoricalStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.common.repository.MenuRecipeRepository;
import com.example.CafeAutoSystem.stock.dto.OrderRequest;
import com.example.CafeAutoSystem.stock.dto.StockLogView;
import com.example.CafeAutoSystem.stock.dto.StockOutResult;
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
    private final SseEmitterManager sseEmitterManager;
    private final HistoricalStockLogRepository historicalStockLogRepository;
    private final StockBatchService stockBatchService;

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

            // 마이너스 재고 방지: 차감 전 현재고 확인 (부족하면 판매 거부)
            int stockBeforeDeduct = currentStockLogRepository.convertToCurrentStock(ingredient.getIngredientId());
            if (stockBeforeDeduct - totalQty < 0) {
                throw new IllegalStateException(
                        String.format("[%s] 재고가 부족합니다. 현재 재고: %d, 필요 수량: %d",
                                ingredient.getIngredientName(), stockBeforeDeduct, totalQty));
            }

            String message = String.format("[판매] %s %d잔 판매",
                    request.getMenuName(), request.getQuantity());

            // STOCK_OUT 로그 INSERT (이벤트 감사 로그)
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

            // FEFO 배치 차감: 유통기한 임박 배치부터 순서대로 remaining_qty 감소
            stockBatchService.deductFefo(ingredient, totalQty);

            log.info("[STOCK_OUT] 재료={} 차감량={}{}",
                    ingredient.getIngredientName(), totalQty, ingredient.getUnit());

            // 현재 재고 SUM 계산 (DB 집계 쿼리)
            int currentStock = currentStockLogRepository
                    .convertToCurrentStock(ingredient.getIngredientId());

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

        // 주문 처리 완료 후 모든 inventory 화면에 실시간 갱신 푸시
        sseEmitterManager.broadcastStockUpdate();

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

    /** 전체 재고 로그 최신순 — 현재(current) + 이력(historical) 통합 (활동 로그 화면용) */
    @Transactional(readOnly = true)
    public List<StockLogView> getAllLogs() {
        List<StockLogView> all = new ArrayList<>();
        for (CurrentStockLogEntity c : currentStockLogRepository.findTop300ByOrderByCreatedAtDesc()) {
            all.add(StockLogView.builder()
                    .source("현재")
                    .ingredientId(c.getIngredient() != null ? c.getIngredient().getIngredientId() : null)
                    .logType(c.getLogType()).message(c.getMessage()).amount(c.getAmount())
                    .reason(c.getReason()).userId(c.getUserId()).createdAt(c.getCreatedAt())
                    .build());
        }
        historicalStockLogRepository.findTop300ByOrderByCreatedAtDesc().forEach(h ->
            all.add(StockLogView.builder()
                    .source("이력")
                    .ingredientId(h.getIngredientId())
                    .logType(h.getLogType()).message(h.getMessage()).amount(h.getAmount())
                    .reason(h.getReason()).userId(h.getUserId()).createdAt(h.getCreatedAt())
                    .build()));
        all.sort((a, b) -> {
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        return all;
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