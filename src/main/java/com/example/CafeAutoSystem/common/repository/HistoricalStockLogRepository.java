package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.HistoricalStockLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalStockLogRepository extends JpaRepository<HistoricalStockLog, Integer> {

    List<HistoricalStockLog> findByIngredientId(Integer ingredientId);

    List<HistoricalStockLog> findByLogType(String logType);
}
