package com.example.CafeAutoSystem.jms_ai_rpa.entity;

import com.example.CafeAutoSystem.global.config.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "HISTORICAL_STOCK_LOG")
public class HistoricalStockLogEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_log_id")
    private Integer histLogId; // 누적로그번호 (PK)

    @Column(name = "ingredient_id", nullable = false)
    private Integer ingredientId; // 식자재번호 (FK)

    @Column(name = "order_item_id")
    private Integer orderItemId; // 발주품목번호 (FK, Null 허용)

    @Column(name = "log_type", nullable = false, length = 50)
    private String logType; // 로그 유형 (STOCK_IN, STOCK_OUT 등)

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message; // 로그 메시지

    @Column(name = "amount", nullable = false)
    private Integer amount; // 발생 수량

    @Column(name = "reason", nullable = false, length = 255)
    private String reason; // 변동 사유

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId = "SYSTEM"; // 수행자 (기본값 SYSTEM)
}
