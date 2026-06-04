package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CurrentStockLogRepository extends JpaRepository<CurrentStockLogEntity, Long> {

    /** 재료 ID로 로그 전체 조회 (재고 SUM용) */
    List<CurrentStockLogEntity> findByIngredient_IngredientId(Integer ingredientId);

    /**
     * 유통기한 만료 재고 조회
     * PURCHASE_ORDER.expiration_date 기준으로 만료된 STOCK_IN 로그 조회
     * amount > 0 인 것만 (이미 폐기된 건 제외)
     */
    @Query("""
        SELECT c FROM CurrentStockLogEntity c
        JOIN PurchaseOrderEntity p ON c.orderItemId = p.orderItemId
        WHERE c.logType = 'STOCK_IN'
          AND c.amount > 0
          AND p.expirationDate IS NOT NULL
          AND p.expirationDate <= :now
    """)
    List<CurrentStockLogEntity> findExpiredStocks(@Param("now") LocalDateTime now);

    /**
     * 재료 ID 기준 현재 재고 SUM
     */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CurrentStockLogEntity c WHERE c.ingredient.ingredientId = :ingredientId")
    int convertToCurrentStock(@Param("ingredientId") Integer ingredientId);
}