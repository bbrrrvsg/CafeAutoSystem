package com.example.CafeAutoSystem.stock.scheduler;

import com.example.CafeAutoSystem.stock.service.StockSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockDiscardScheduler {

    private final StockSchedulerService stockSchedulerService;

    /**
     * 매일 밤 11시(23:00) 유통기한 만료 자재 자동 폐기
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void processExpiredStockDiscard() {
        log.info("=== [배치 가동] 유통기한 만료 자재 자동 폐기 스케줄러 시작 ===");
        try {
            stockSchedulerService.discardExpiredIngredients();
            log.info("=== [배치 완료] 유통기한 만료 자재 자동 폐기 스케줄러 정상 종료 ===");
        } catch (Exception e) {
            log.error("!!! [배치 장애] 자동 폐기 처리 중 에러 발생: {}", e.getMessage(), e);
        }
    }
}