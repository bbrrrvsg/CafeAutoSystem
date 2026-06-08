package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.HistoricalStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 월말 재고 장부 마감 마이그레이션.
 * 매월 1일 0시 1회 실행. 순서 절대 준수: 백업 → 집계 → 삭제 → 이월.
 * 네 단계가 한 트랜잭션이라 중간에 실패하면 전부 롤백된다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockMigrationService {

    private final HistoricalStockLogRepository historicalStockLogRepository;
    private final CurrentStockLogRepository currentStockLogRepository;
    private final IngredientRepository ingredientRepository;


    // 현재 로그 과거 로그에 복붙 후 집계하고 변수에 저장 그리고 테이블 비우고 집계한 수량 반영
    //@Scheduled(cron = "0 * * * * * ")//초 분 시 일 월 요일  모든 시간에서 0초 1분 마다 실행(테스트용)
    @Scheduled(cron = "0 0 23 * * *")
    @Transactional
    public void backup(){
        // 1) 백업
        int backupCount = historicalStockLogRepository.backup();
        System.out.println("backupCount = " + backupCount);
        if (backupCount == 0) return;

        // 2) 집계 (삭제 前, 변수에 보관)
        var sums = currentStockLogRepository.HisSum();

        // 3) 삭제
        currentStockLogRepository.deleteAllInBatch();

        // 4) 이월 (삭제 後)
        for (var s : sums) {
            IngredientEntity ing = ingredientRepository.getReferenceById(s.getIngredientId());
            currentStockLogRepository.save(CurrentStockLogEntity.builder()
                    .ingredient(ing)
                    .logType("STOCK_FORWARD")
                    .amount(s.getTotalAmount())
                    .reason("전월 재고 이월")
                    .message("[이월] " + ing.getIngredientName() + " 전월 마감 잔량 " + s.getTotalAmount())
                    .userId("SYSTEM")
                    .build());
        }
    }



}
