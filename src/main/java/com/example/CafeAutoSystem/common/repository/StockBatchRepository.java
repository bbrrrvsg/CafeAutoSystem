package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.StockBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatchEntity, Long> {

    /**
     * FEFO 차감용: 재료별 잔여 배치를 유통기한 오름차순 조회
     * remaining_qty > 0 인 배치만 (소진 배치 제외)
     */
    List<StockBatchEntity> findByIngredient_IngredientIdAndRemainingQtyGreaterThanOrderByExpiryDateAsc(
            Integer ingredientId, int minRemaining);

    /**
     * 재료별 현재고 합계 (배치 remaining_qty SUM)
     */
    @Query("SELECT COALESCE(SUM(b.remainingQty), 0) FROM StockBatchEntity b " +
           "WHERE b.ingredient.ingredientId = :ingredientId")
    int sumRemainingByIngredientId(@Param("ingredientId") Integer ingredientId);

    /**
     * 만료 배치 조회: expiry_date <= 오늘 AND remaining_qty > 0
     * 폐기 스케줄러가 사용
     */
    @Query("SELECT b FROM StockBatchEntity b " +
           "WHERE b.expiryDate IS NOT NULL AND b.expiryDate <= :today AND b.remainingQty > 0")
    List<StockBatchEntity> findExpiredBatches(@Param("today") LocalDate today);

    /**
     * 만료 임박 배치 조회: 오늘~D+3 이내 만료 예정
     * 대시보드 경고용
     */
    @Query("SELECT b FROM StockBatchEntity b " +
           "WHERE b.expiryDate IS NOT NULL " +
           "AND b.expiryDate BETWEEN :today AND :warnDate " +
           "AND b.remainingQty > 0 " +
           "ORDER BY b.expiryDate ASC")
    List<StockBatchEntity> findExpiringBatches(
            @Param("today") LocalDate today,
            @Param("warnDate") LocalDate warnDate);
}
