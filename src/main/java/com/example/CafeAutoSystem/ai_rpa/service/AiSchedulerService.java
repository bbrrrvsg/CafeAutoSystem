package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSchedulerService {
    private final AiPredictService aiPredictService;
    /**
     * ⏰ 매일 밤 11시(23시) 정각에 자동으로 실행되는 AI 통계 예측 엔진
     * * [운영 배포용 크론 설정]
     * @Scheduled(cron = "0 0 23 * * *")
     * *  - 현재는 5초마다 돌면서 로직을 검증
     */
    //@Scheduled(initialDelay = 2000, fixedRate = 5000) // 구동 시험을 위해 5초마다 실행
    @Scheduled(cron = "0 0 23 * * *")
    public void runDailyAiStockAnalysis() {
        log.info("==================================================================");
        log.info("🤖 [자바 스케줄러] 파이썬 AI 예측 서버(FastAPI)와 HTTP REST API 통신 시작");
        log.info("==================================================================");

        try {
            Integer targetIngredientId = 4;
            String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString();

            // 1. 자바가 직접 리포지토리로 최근 4주 DB 로그 리스트 긁어오기!
            List<HistoricalStockLogEntity> logEntities = aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

            // 엔티티에서 수량(Amount)들만 쏙 뽑아서 Double 리스트로 변환
            List<Integer> amountList = new ArrayList<>();
            for (HistoricalStockLogEntity entity : logEntities) {
                amountList.add(entity.getAmount());
            }

            // 2. 파이썬 서버로 보낼 가방에 데이터 리스트도 함께 패킹!
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("ingredientId", targetIngredientId);
            requestBody.put("dayOfWeek", todayDayOfWeek);
            requestBody.put("rawAmounts", amountList); // ✨ 자바가 조회한 데이터를 파이썬에게 토스!

            // 3. 파이썬 서버 호출
            String pythonServerUrl = "http://127.0.0.1:8000/order/predict";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject(pythonServerUrl, requestBody, Map.class);

            if (response != null) {
                log.info("📊 [HTTP 응답 수거 성공!] 파이썬 서버 리턴값: {}", response);

                // 파이썬 response_model(PredictionResponse) 구조대로 꺼내기
                String status = (String) response.get("status");          // "AI_PREDICT" 또는 "AI_ERROR"
                Integer suggestedQty = (Integer) response.get("suggestedQty"); // 추천 발주 수량
                String message = (String) response.get("message");         // 요약 메시지
                String code = (String) response.get("code");               // "AUTO_ANALYSIS" 등

                // ──────────────────────────────────────────────────────────────
                // 💡 [STEP 4] 파이썬이 연산해준 따끈따끈한 데이터 그대로 DB 적재!
                // ──────────────────────────────────────────────────────────────
                aiPredictService.saveAiAnalysisLog(
                        targetIngredientId,
                        status,
                        suggestedQty,
                        message,
                        code
                );
                log.info("🎉 [DB 적재 완료] 파이썬 AI 분석 결과 장부 저장 완료.");

            } else {
                log.error("❌ [통신 실패] 파이썬 서버로부터 응답 빈 값이 넘어왔습니다.");
            }

        } catch (Exception e) {
            log.error("❌ [AI 예측 엔진 오류] 파이썬 FastAPI 서버와 HTTP 통신 중 장애 발생: {}", e.getMessage());
        }

        log.info("==================================================================");
    }
}