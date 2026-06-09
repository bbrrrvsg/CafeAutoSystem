package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalStockLogRepository extends JpaRepository<HistoricalStockLogEntity, Integer> {

    List<HistoricalStockLogEntity> findByIngredientIdOrderByCreatedAtAsc(Integer ingredientId);

    List<HistoricalStockLogEntity> findByLogType(String logType);

    /** 전체 이력 로그 최신순 (통합 로그 화면용, 최근 300건) */
    List<HistoricalStockLogEntity> findTop300ByOrderByCreatedAtDesc();


    // 백업
    @Modifying
    @Query(value = """
        INSERT INTO historical_stock_log
            (ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at)
        SELECT ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at
        FROM current_stock_log
        """, nativeQuery = true)
    int backup(); // 영향받은 행수 반환

    @Query("""
    SELECT h FROM HistoricalStockLogEntity h
    WHERE h.ingredientId = :ingredientId
    AND h.logType = 'AI_PREDICT'
    ORDER BY h.createdAt DESC
    """)
        List<HistoricalStockLogEntity> findLatestAiLogs(Integer ingredientId);
}
