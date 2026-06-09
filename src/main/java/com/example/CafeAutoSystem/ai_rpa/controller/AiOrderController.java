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
        log.info("📊 [AI 대시보드] 정석 데이터 바인딩 및 3일 누적 연산 동기화 주행");

        try {
            List<OrderItemDto> orderList = new ArrayList<>();
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();

            for (IngredientEntity ingredient : allIngredients) {
                Integer ingredientId = ingredient.getIngredientId();

                // 1. 실시간 현재고 수거 및 음수 방어
                int currentStock = currentStockLogRepository.convertToCurrentStock(ingredientId);
                if (currentStock < 0) {
                    currentStock = 0;
                }

                int safetyStock = ingredient.getSafetyStock();

                // 2. 최신 AI 예측 발주량 데이터 수거
                List<HistoricalStockLogEntity> aiLogs =
                        historicalStockLogRepository.findLatestAiLogs(ingredientId);

                int aiSuggestedStockQty = 0;

                if (aiLogs != null && !aiLogs.isEmpty()) {
                    aiSuggestedStockQty = aiLogs.get(0).getAmount(); // 최신 1개
                }

                // 3. 거래처 정보 및 환산 계수(factor) 추출
                int unitPrice = 0;
                int factor = 1;

                List<VendorIngredientEntity> mappingDetails =
                        vendorIngredientRepository.findByIngredient_IngredientIdOrderByPriorityRankAsc(ingredientId);

                if (mappingDetails != null && !mappingDetails.isEmpty()) {
                    VendorIngredientEntity mainVendorIngredient = mappingDetails.get(0);
                    unitPrice = mainVendorIngredient.getUnitPrice();
                    factor = mainVendorIngredient.getIngredient().unitPerOrderOrDefault();
                } else {
                    factor = ingredient.unitPerOrderOrDefault();
                }

                // 4. [하드코딩 방어벽 걷어내기]
                int displayOrderQty = aiSuggestedStockQty;

                // 5. 현재고와 안전재고를 화면 규격(팩, 봉)으로 분할 환산
                int calculatedCurrentStock = (int) Math.round((double) currentStock / factor);
                int calculatedSafetyStock = (int) Math.round((double) safetyStock / factor);

                // 6. 예상 필요량 칸에 '3일간의 순수 소모 예측량' 역산 대입
                int calculatedPredictedRequiredQty = displayOrderQty - calculatedSafetyStock + calculatedCurrentStock;
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
                        .unitPrice(unitPrice)
                        .totalPrice(totalPrice)
                        .build();

                orderList.add(dto);
            }

            int totalOrderPrice = 0;
            for (OrderItemDto item : orderList) {
                totalOrderPrice += item.getTotalPrice();
            }

            model.addAttribute("orderList", orderList);
            model.addAttribute("totalOrderPrice", String.format("%,d", totalOrderPrice));
            model.addAttribute("aiStatus", "NORMAL");

        } catch (Exception e) {
            log.error("❌ AI 대시보드 데이터 바인딩 중 크리티컬 장애 발생: {}", e.getMessage());
        }

        return "ai-order/ai-order";
    }
}