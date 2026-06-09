package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.AiPredictRequestDto;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSchedulerService {
    private final AiPredictService aiPredictService;
    private final CurrentStockLogRepository currentStockLogRepository;
    private final IngredientRepository ingredientRepository;

    @Scheduled(initialDelay = 2000, fixedRate = 99999999)
    public void runDailyAiStockAnalysis() {
        log.info("🤖 [AI 스케줄러] 전 자재 대상 PyTorch 딥러닝 분석 및 장부 병합 시작");

        try {
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();
            String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString();

            RestTemplate restTemplate = new RestTemplate();
            String pythonServerUrl = "http://127.0.0.1:8000/order/predict";

            for (IngredientEntity ingredient : allIngredients) {
                Integer targetIngredientId = ingredient.getIngredientId();
                log.info("📊 [자재 분석] ID: {} | 이름: {}", targetIngredientId, ingredient.getIngredientName());

                List<HistoricalStockLogEntity> logEntities = aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

                List<CurrentStockLogEntity> currentLogEntities =
                        currentStockLogRepository.findByIngredient_IngredientIdOrderByCreatedAtAsc(targetIngredientId);

                List<Integer> amountList = new ArrayList<>();

                for (HistoricalStockLogEntity entity : logEntities) {
                    amountList.add(entity.getAmount());;
                }

                if (currentLogEntities != null) {
                    for (CurrentStockLogEntity entity : currentLogEntities) {
                        amountList.add(entity.getAmount());
                    }
                }

                log.info("📈 자재 ID {}: 총 {}개의 시계열 학습 데이터 수집 완료", targetIngredientId, amountList.size());

                int currentStockSum = currentStockLogRepository.convertToCurrentStock(targetIngredientId);
                int realSafetyStock = aiPredictService.getSafetyStockByIngredient(targetIngredientId);

                AiPredictRequestDto requestDto = AiPredictRequestDto.builder()
                        .ingredientId(targetIngredientId)
                        .dayOfWeek(todayDayOfWeek)
                        .currentStock(currentStockSum)
                        .safetyStock(realSafetyStock)
                        .rawAmounts(amountList)
                        .build();

                Map<String, Object> response = restTemplate.postForObject(pythonServerUrl, requestDto, Map.class);

                if (response != null) {
                    String status = (String) response.get("status");
                    Integer suggestedQty = (Integer) response.get("suggestedQty");
                    String message = (String) response.get("message");
                    String code = (String) response.get("code");

                    aiPredictService.saveAiAnalysisLog(targetIngredientId, status, suggestedQty, message, code);
                    log.info("🎉 자재 ID {}번 분석 결과 장부 적재 완료\n", targetIngredientId);
                } else {
                    log.error("❌ 자재 ID {}번 파이썬 서버 응답 누락\n", targetIngredientId);
                }
            }

        } catch (Exception e) {
            log.error("❌ [AI 예측 엔진 오류] 분석 순회 중 장애 발생: {}", e.getMessage());
        }
    }
}