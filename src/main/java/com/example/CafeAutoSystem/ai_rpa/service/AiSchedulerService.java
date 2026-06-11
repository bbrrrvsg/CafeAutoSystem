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

    // 파이썬 AI 배포 서버 기본 베이스 URL 주소
    // "http://cafe-ai-system-env.eba-ppyuzfmm.ap-northeast-2.elasticbeanstalk.com/order/predict" 배포 서버 링크
    private final String pythonServerBaseUrl = "http://127.0.0.1:8000/order";


    @Scheduled(cron = "0 0 22 * * *")
    public void runDailyAiStockAnalysis() {
        log.info("[AI 스케줄러] 전 자재 대상 PyTorch 딥러닝 기반 실시간 초고속 발주 제안 연산 시작");

        try {
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();
            String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString();
            RestTemplate restTemplate = new RestTemplate();
            String predictUrl = pythonServerBaseUrl + "/predict";

            for (IngredientEntity ingredient : allIngredients) {
                Integer targetIngredientId = ingredient.getIngredientId();
                log.info("[자재 예측 조회] ID: {} | 이름: {}", targetIngredientId, ingredient.getIngredientName());

                // 공통 헬퍼 메서드를 통해 전체 원장 로그 데이터 수거
                List<Integer> amountList = gatherRawAmounts(targetIngredientId);
                log.info("자재 ID {}: 총 {}개의 시계열 검증 데이터 윈도우 조립 완료", targetIngredientId, amountList.size());

                int currentStockSum = currentStockLogRepository.convertToCurrentStock(targetIngredientId);
                int realSafetyStock = aiPredictService.getSafetyStockByIngredient(targetIngredientId);

                AiPredictRequestDto requestDto = AiPredictRequestDto.builder()
                        .ingredientId(targetIngredientId)
                        .dayOfWeek(todayDayOfWeek)
                        .currentStock(currentStockSum)
                        .safetyStock(realSafetyStock)
                        .rawAmounts(amountList)
                        .build();

                Map<String, Object> response = restTemplate.postForObject(predictUrl, requestDto, Map.class);

                if (response != null) {
                    String status = (String) response.get("status");
                    Integer suggestedQty = (Integer) response.get("suggestedQty");
                    String message = (String) response.get("message");
                    String code = (String) response.get("code");

                    aiPredictService.saveAiAnalysisLog(targetIngredientId, status, suggestedQty, message, code);
                    log.info("자재 ID {}번 AI 실시간 제안량 장부 적재 완료\n", targetIngredientId);
                } else {
                    log.error("자재 ID {}번 파이썬 예측 서버 응답 누락\n", targetIngredientId);
                }
            }
        } catch (Exception e) {
            log.error("[AI 예측 엔진 오류] 분석 순회 중 장애 발생: {}", e.getMessage());
        }
    }


    // @Scheduled(cron = "0 0 3 1 * *")
    @Scheduled(initialDelay = 5000, fixedDelay = 315360000000L)
    public void runMonthlyAiModelTraining() {
        log.info("[AI 스케줄러] 매달 1일 PyTorch LSTM 신경망 가중치 정기 배치 학습 프로세스 가동");

        try {
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();
            RestTemplate restTemplate = new RestTemplate();
            String trainUrl = pythonServerBaseUrl + "/train";

            for (IngredientEntity ingredient : allIngredients) {
                Integer targetIngredientId = ingredient.getIngredientId();
                log.info("[모델 학습 주입] 자재 ID: {} | 이름: {}", targetIngredientId, ingredient.getIngredientName());

                // 공통 헬퍼 메서드를 통해 대량의 학습용 원장 데이터 수거
                List<Integer> amountList = gatherRawAmounts(targetIngredientId);
                log.info("자재 ID {}: 총 {}개의 원장 히스토리 학습 데이터 파이썬 전송 예정", targetIngredientId, amountList.size());

                // 파이썬 TrainRequest 규격 스키마 데이터 구조 매핑
                Map<String, Object> requestPayload = Map.of(
                        "ingredientId", targetIngredientId,
                        "rawAmounts", amountList
                );

                Map<String, Object> response = restTemplate.postForObject(trainUrl, requestPayload, Map.class);

                if (response != null) {
                    log.info("자재 ID {}번 정기 역전파 가중치 훈련 완료: {}", targetIngredientId, response.get("message"));
                } else {
                    log.error("자재 ID {}번 파이썬 배치 학습 서버 응답 백랭크 누락", targetIngredientId);
                }
            }
            log.info("전 자재에 대한 PyTorch LSTM 가중치 파일(.pt) 정기 동기화 완료.");
        } catch (Exception e) {
            log.error("[AI 학습 엔진 오류] 배치 정기 학습 순회 중 장애 발생: {}", e.getMessage());
        }
    }


    private List<Integer> gatherRawAmounts(Integer targetIngredientId) {
        List<HistoricalStockLogEntity> logEntities =
                aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

        List<CurrentStockLogEntity> currentLogEntities =
                currentStockLogRepository.findByIngredient_IngredientIdOrderByCreatedAtAsc(targetIngredientId);

        List<Integer> amountList = new ArrayList<>();

        if (logEntities != null) {
            for (HistoricalStockLogEntity entity : logEntities) {
                amountList.add(entity.getAmount());
            }
        }

        if (currentLogEntities != null) {
            for (CurrentStockLogEntity entity : currentLogEntities) {
                amountList.add(entity.getAmount());
            }
        }
        return amountList;
    }
}