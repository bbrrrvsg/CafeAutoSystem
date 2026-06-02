package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalStockLogRepository extends JpaRepository<HistoricalStockLogEntity, Integer> {

    List<HistoricalStockLogEntity> findByIngredientId(Integer ingredientId);

    List<HistoricalStockLogEntity> findByLogType(String logType);
}
