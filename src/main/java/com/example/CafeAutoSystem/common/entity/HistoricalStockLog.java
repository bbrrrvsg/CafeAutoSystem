package com.example.CafeAutoSystem.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historical_stock_log")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HistoricalStockLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_log_id")
    private Integer histLogId;

    @Column(name = "ingredient_id", nullable = false)
    private Integer ingredientId;

    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Column(name = "log_type", nullable = false, length = 50)
    private String logType;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
}
