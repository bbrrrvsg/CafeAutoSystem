package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.AiPredictRequestDto;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
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

    // @Scheduled(cron = "0 0 23 * * *") // 실운영용
    @Scheduled(initialDelay = 2000, fixedRate = 99999999) // 🧪 딱 1번만 순회 테스트용
    public void runDailyAiStockAnalysis() {
        log.info("==================================================================");
        log.info("🤖 [자바 스케줄러] 전 자재 대상 PyTorch 딥러닝 분석 순회 시작");
        log.info("==================================================================");

        try {
            // 디비에서 모든 식자재 리스트 조회
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();
            String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString();

            RestTemplate restTemplate = new RestTemplate();
            String pythonServerUrl = "http://127.0.0.1:8000/order/predict";

            // 모든 자재를 하나씩 돌면서 파이썬과 통신합니다.
            for (IngredientEntity ingredient : allIngredients) {
                Integer targetIngredientId = ingredient.getIngredientId();
                log.info("📊 [자재 분석 중] ID: {} | 이름: {}", targetIngredientId, ingredient.getIngredientName());

                // 과거 DB 로그 리스트 긁어오기
                List<HistoricalStockLogEntity> logEntities = aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

                // 소모량 절대값 가공
                List<Integer> amountList = new ArrayList<>();
                for (HistoricalStockLogEntity entity : logEntities) {
                    amountList.add(Math.abs(entity.getAmount()));
                }

                // 현재고 및 안전재고 조회
                int currentStockSum = currentStockLogRepository.convertToCurrentStock(targetIngredientId);
                int realSafetyStock = aiPredictService.getSafetyStockByIngredient(targetIngredientId);

                // DTO 가방 패킹
                AiPredictRequestDto requestDto = AiPredictRequestDto.builder()
                        .ingredientId(targetIngredientId)
                        .dayOfWeek(todayDayOfWeek)
                        .currentStock(currentStockSum)
                        .safetyStock(realSafetyStock)
                        .rawAmounts(amountList)
                        .build();

                // 파이썬 서버 호출 (루프 돌 때마다 각 자재 데이터 전송)
                Map<String, Object> response = restTemplate.postForObject(pythonServerUrl, requestDto, Map.class);

                if (response != null) {
                    log.info("📥 [응답 수거 성공] 자재 ID {}번 결과: {}", targetIngredientId, response);

                    String status = (String) response.get("status");
                    Integer suggestedQty = (Integer) response.get("suggestedQty");
                    String message = (String) response.get("message");
                    String code = (String) response.get("code");

                    // DB 결과 적재
                    aiPredictService.saveAiAnalysisLog(
                            targetIngredientId,
                            status,
                            suggestedQty,
                            message,
                            code
                    );
                    log.info("🎉 [적재 완료] 자재 ID {}번 분석 장부 저장 성공.\n", targetIngredientId);

                } else {
                    log.error("❌ [통신 실패] 자재 ID {}번 응답 값이 비어있습니다.\n", targetIngredientId);
                }
            }

        } catch (Exception e) {
            log.error("❌ [AI 예측 엔진 오류] 분석 순회 중 장애 발생: {}", e.getMessage());
        }

        log.info("==================================================================");
    }
}