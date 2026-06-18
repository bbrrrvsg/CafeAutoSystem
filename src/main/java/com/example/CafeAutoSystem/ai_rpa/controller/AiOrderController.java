package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import com.example.CafeAutoSystem.ai_rpa.service.AiPredictService;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.VendorIngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.HistoricalStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.common.repository.VendorIngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AiOrderController {

    private final IngredientRepository ingredientRepository;
    private final CurrentStockLogRepository currentStockLogRepository;
    private final AiPredictService aiPredictService;
    private final VendorIngredientRepository vendorIngredientRepository;
    private final HistoricalStockLogRepository historicalStockLogRepository;

    @GetMapping("/ai-order")
    @Transactional(readOnly = true)
    public String aiOrderPage(Model model) {
        log.info("📊 [AI 대시보드] 실시간 재분석 유연 데이터 바인딩 가동");

        try {
            List<OrderItemDto> orderList = new ArrayList<>();
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();

            LocalDate todayDate = LocalDate.now();
            LocalDate yesterdayDate = LocalDate.now().minusDays(1);

            boolean hasPredictData = false;

            for (IngredientEntity ingredient : allIngredients) {
                Integer ingredientId = ingredient.getIngredientId();

                int currentStock = currentStockLogRepository.convertToCurrentStock(ingredientId);
                if (currentStock < 0) {
                    currentStock = 0;
                }

                int safetyStock = ingredient.getSafetyStock();

                List<HistoricalStockLogEntity> aiLogs =
                        historicalStockLogRepository.findByIngredientIdAndLogDate(ingredientId, todayDate);

                if (aiLogs == null || aiLogs.isEmpty()) {
                    aiLogs = historicalStockLogRepository.findByIngredientIdAndLogDate(ingredientId, yesterdayDate);
                }

                int aiSuggestedStockQty = 0;
                if (aiLogs != null && !aiLogs.isEmpty()) {
                    aiSuggestedStockQty = aiLogs.get(0).getAmount();
                    hasPredictData = true;
                }

                int unitPrice = 0;
                List<VendorIngredientEntity> mappingDetails =
                        vendorIngredientRepository.findByIngredient_IngredientIdOrderByPriorityRankAsc(ingredientId);

                if (mappingDetails != null && !mappingDetails.isEmpty()) {
                    unitPrice = mappingDetails.get(0).getUnitPrice();
                }

                int displayOrderQty = aiSuggestedStockQty;
                if ("g".equals(ingredient.getUnit()) || "ml".equals(ingredient.getUnit())) {
                    displayOrderQty = (int) Math.ceil(displayOrderQty / 1000.0) * 1000;
                }

                int calculatedCurrentStock = currentStock;

                int calculatedPredictedRequiredQty = displayOrderQty - safetyStock + calculatedCurrentStock;
                if (calculatedPredictedRequiredQty < 0) {
                    calculatedPredictedRequiredQty = 0;
                }

                int totalPrice = displayOrderQty * unitPrice;

                OrderItemDto dto = OrderItemDto.builder()
                        .ingredientId(ingredientId)
                        .ingredientName(ingredient.getIngredientName())
                        .orderQty(displayOrderQty)
                        .predictedRequiredQty(calculatedPredictedRequiredQty)
                        .currentStock(calculatedCurrentStock)
                        .safetyStock(safetyStock)
                        .unitPrice(unitPrice)
                        .totalPrice(totalPrice)
                        .ingredientUnit(ingredient.getUnit())
                        .build();

                orderList.add(dto);
            }

            int totalOrderPrice = 0;
            for (OrderItemDto item : orderList) {
                totalOrderPrice += item.getTotalPrice();
            }

            model.addAttribute("orderList", orderList);
            model.addAttribute("totalOrderPrice", String.format("%,d", totalOrderPrice));

            if (!hasPredictData) {
                model.addAttribute("aiStatus", "EMPTY");
            } else {
                model.addAttribute("aiStatus", "NORMAL");
            }

        } catch (Exception e) {
            log.error("❌ AI 대시보드 데이터 바인딩 중 크리티컬 장애 발생: {}", e.getMessage());
            model.addAttribute("aiStatus", "AI_ERROR");
        }

        return "ai-order/ai-order";
    }
}