package com.example.CafeAutoSystem.jms_ai_rpa.service;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.HistoricalStockLogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

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
    //@Scheduled(initialDelay = 2000, fixedRate = 5000) // 5초마다 가동
    @Scheduled(cron = "0 0 23 * * *")
    public void runDailyAiStockAnalysis() {
        log.info("==================================================================");
        log.info("🤖 [AI 통계 예측 엔진] 요일별 소모량 분석 알고리즘 가동");
        log.info("==================================================================");

        try {
            LocalDate today = LocalDate.now();
            DayOfWeek todayDayOfWeek = today.getDayOfWeek();
            log.info("-> [시스템 날짜] 분석 기준일: {} ({})", today, todayDayOfWeek);

            Integer targetIngredientId = 4;
            log.info("-> [AI 분석 대상] 자재 ID: 4 (서울우유 1000ml)");

            List<HistoricalStockLogEntity> allLogs = aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

            int matchingDayCount = 0;
            double totalConsumedAmount = 0.0;

            for (HistoricalStockLogEntity logItem : allLogs) {
                LocalDate logDate = logItem.getCreatedAt().toLocalDate();

                if (logDate.getDayOfWeek() == todayDayOfWeek) {
                    if ("STOCK_DISCARD".equals(logItem.getLogType()) || "STOCK_CONSUME".equals(logItem.getLogType())) {

                        double currentAmount = logItem.getAmount();

                        //  음수(-) 데이터는 아예 통계 샘플에서 제외(Drop)합니다.
                        if (currentAmount < 0) {
                            log.warn("⚠️ [데이터 정제] 오염된 음수 데이터 발견 (Log ID: {}, 값: {}). 통계 계산에서 제외합니다.", logItem.getHistLogId(), currentAmount);
                            continue;
                        }

                        totalConsumedAmount += currentAmount;
                        matchingDayCount++; // 정상적인 데이터만 건수를 셉니다.
                    }
                }
            }

            log.info("-> [통계 필터링 결과] 최근 4주간 동일 요일({}) 데이터 총 {}건 매칭 완료.", todayDayOfWeek, matchingDayCount);

            double averageConsumed = 0.0;
            if (matchingDayCount > 0) {
                averageConsumed = totalConsumedAmount / matchingDayCount;
            }
            log.info("-> [수식 계산 완성] 최근 4주 요일별 평균 소모량: {} 개", Math.round(averageConsumed * 10) / 10.0);

            // 가중치 1.2배 적용하여 추천 발주량 산출
            int suggestedOrderQty = (int) Math.ceil(averageConsumed * 1.2);

            if (suggestedOrderQty <= 0 || suggestedOrderQty > 100) {
                log.error("❌❌❌ [이상치 발생 (AI_ERROR)] 산출된 발주 권장량({})이 허용 범위를 벗어났습니다!", suggestedOrderQty);
                log.error("⚠️ [시스템 안전 동결] 안전을 위해 내일 권장 발주량을 기본 안전재고 [ 10개 ]로 강제 동결하고 DB에 기록합니다.");

                suggestedOrderQty = 10; // 10개로 안전하게 고정

                // 🌟 [추가] DB 저장: 이상치 발생 및 10개 강제 동결 장애 로그 누적
                aiPredictService.saveAiAnalysisLog(
                        targetIngredientId,
                        "AI_ERROR",
                        suggestedOrderQty, // 10 진입 (Integer)
                        "요일별 분석 이상치 감지 및 시스템 동결 발생",
                        "SYSTEM_FREEZE"
                );

            } else {
                log.info("📢 [AI 예측 최종 제안] 내일 추천 발주량: {} 개", suggestedOrderQty);

                // 🌟 [추가] DB 저장: 정상적인 AI 분석 완료 및 내일 권장 발주량 적재
                aiPredictService.saveAiAnalysisLog(
                        targetIngredientId,
                        "AI_PREDICT",
                        suggestedOrderQty, // 산출된 정수값 진입 (Integer)
                        "최근 4주 " + todayDayOfWeek + " 통계 기반 자동 산출 완료",
                        "AUTO_ANALYSIS"
                );
            }

        } catch (Exception e) {
            log.error("❌ [AI 예측 엔진 오류] 가동 중 크리티컬 장애 발생: {}", e.getMessage());
        }

        log.info("==================================================================");
    }
}
