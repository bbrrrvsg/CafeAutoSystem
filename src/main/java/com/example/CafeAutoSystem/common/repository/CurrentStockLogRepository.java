package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurrentStockLogRepository extends JpaRepository<CurrentStockLogEntity, Long> {

    List<CurrentStockLogEntity> findByIngredientId(Integer ingredientId);

    /** 재료별 로그 최신순 조회 */
    List<CurrentStockLogEntity> findByIngredientIdOrderByCreatedAtDesc(Long ingredientId);
}
