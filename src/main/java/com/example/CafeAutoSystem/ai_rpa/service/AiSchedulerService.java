package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.AiPredictRequestDto;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
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
    private final CurrentStockLogRepository currentStockLogRepository; // 🔌 의존성 주입 완료!

    // @Scheduled(cron = "0 0 23 * * *")
    @Scheduled(initialDelay = 2000, fixedRate = 99999999) // 🧪 5초 테스트용
    public void runDailyAiStockAnalysis() {
        log.info("==================================================================");
        log.info("🤖 [자바 스케줄러] 파이썬 AI 예측 서버(FastAPI)와 DTO 통신 시작");
        log.info("==================================================================");

        try {
            // 💡 [개선] 향후 모든 재료를 돌릴 수 있도록 타겟 ID 변수화 가능
            Integer targetIngredientId = 4;
            String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString();

            // 1. 과거 4주 DB 로그 리스트 긁어오기
            List<HistoricalStockLogEntity> logEntities = aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

            // 2. 텍스트 다이어트: 순수 소모량(amount)만 추출 (소모량 분석이므로 음수만 추출하거나 절대값 가공 가능)
            List<Integer> amountList = new ArrayList<>();
            for (HistoricalStockLogEntity entity : logEntities) {
                // 발주나 입고(+) 데이터가 섞여 있다면 거르고, 순수 판매 소모량(-)만 추리기 원할 시 조건 추가 가능
                // 여기서는 일단 들어온 값을 절대값으로 양수화해서 파이썬이 계산하기 편하게 넘겨줍니다.
                amountList.add(Math.abs(entity.getAmount()));
            }

            // 3. ✨ 민서님이 만들어둔 레포지토리 메서드로 이 자재의 진짜 현재 남은 재고 SUM 계산!
            int currentStockSum = currentStockLogRepository.convertToCurrentStock(targetIngredientId);
            int mockSafetyStock = 50; // 🧪 테스트용 임시 안전재고 값
            // 4. 하드코딩 Map 대신 정석 DTO 가방에 담아서 패킹 완료!
            AiPredictRequestDto requestDto = AiPredictRequestDto.builder()
                    .ingredientId(targetIngredientId)
                    .dayOfWeek(todayDayOfWeek)
                    .currentStock(currentStockSum) // 현재고 반영 성공
                    .safetyStock(mockSafetyStock)
                    .rawAmounts(amountList)
                    .build();

            // 5. 파이썬 서버 호출
            String pythonServerUrl = "http://127.0.0.1:8000/order/predict";
            RestTemplate restTemplate = new RestTemplate();

            // Map 대신 requestDto 객체를 다이렉트로 넘겨 통신 구조화
            Map<String, Object> response = restTemplate.postForObject(pythonServerUrl, requestDto, Map.class);

            if (response != null) {
                log.info("📊 [HTTP 응답 수거 성공!] 파이썬 서버 리턴값: {}", response);

                String status = (String) response.get("status");
                Integer suggestedQty = (Integer) response.get("suggestedQty");
                String message = (String) response.get("message");
                String code = (String) response.get("code");

                // DB 적재
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
            log.error("❌ [AI 예측 엔진 오류] 파이썬 서버와 통신 중 장애 발생: {}", e.getMessage());
        }

        log.info("==================================================================");
    }
}