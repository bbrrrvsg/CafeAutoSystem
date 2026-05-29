package com.example.CafeAutoSystem.jms_ai_rpa.repository;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.HistoricalStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalStockLogRepository extends JpaRepository<HistoricalStockLogEntity, Integer> {

    //AI 알고리즘이 특정 식자재(예: 우유 ID=4)의 과거 소모 패턴 로그만 역추적할 때 사용
    List<HistoricalStockLogEntity> findByIngredientId(Integer ingredientId);

    //특정 로그 유형(예: AI_VALIDATION, RPA_RETRY)만 따로 모아서 타임라인 화면에 뿌릴 때 사용
    List<HistoricalStockLogEntity> findByLogType(String logType);
}
