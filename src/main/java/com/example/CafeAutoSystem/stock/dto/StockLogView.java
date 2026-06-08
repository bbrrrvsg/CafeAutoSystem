package com.example.CafeAutoSystem.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 재고 로그 통합 뷰 (current_stock_log + historical_stock_log 공통 모양) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLogView {
    private String        source;       // "현재" / "이력"
    private Integer       ingredientId;
    private String        logType;
    private String        message;
    private Integer       amount;
    private String        reason;
    private String        userId;
    private LocalDateTime createdAt;
}
