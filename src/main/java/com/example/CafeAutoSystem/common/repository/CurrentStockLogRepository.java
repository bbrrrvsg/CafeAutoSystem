package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CurrentStockLogRepository extends JpaRepository<CurrentStockLogEntity, Long> {

    List<CurrentStockLogEntity> findByIngredient_IngredientId(Long ingredientId);

    /** 재료별 로그 최신순 조회 */
    List<CurrentStockLogEntity> findByIngredient_IngredientIdOrderByCreatedAtDesc(Long ingredientId);

    // 현제 로그 집계 후 첫행에 총수량 반영을 위해
    @Query("""
            SELECT c.ingredient.ingredientId AS ingredientId, SUM(c.amount) AS totalAmount
            FROM CurrentStockLogEntity c
            GROUP BY c.ingredient.ingredientId
            """)
    List<StockSumProjection>HisSum();
    // 디비만 업데이트 하기 때문에 dto 사용안하고 인터페이스 프로젝션 사용
    interface StockSumProjection{
        Long getIngredientId();
        Long getTotalAmount();

    }

}
