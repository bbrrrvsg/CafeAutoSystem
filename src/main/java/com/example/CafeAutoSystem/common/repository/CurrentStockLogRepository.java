package com.example.CafeAutoSystem.jms_ai_rpa.repository;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.CurrentStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurrentStockLogRepository extends JpaRepository<CurrentStockLogEntity, Long> {

    List<CurrentStockLogEntity> findByIngredient_IngredientId(Long ingredientId);

    /** 재료별 로그 최신순 조회 */
    List<CurrentStockLogEntity> findByIngredient_IngredientIdOrderByCreatedAtDesc(Long ingredientId);
}
