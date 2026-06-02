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
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// 당월 실시간 재고 임시 장부 (매월 1일 리셋)
@Entity
@Table(name = "current_stock_log")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CurrentStockLogEntity {

    // 로그번호 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    // 식자재번호 (FK)
    @Column(name = "ingredient_id", nullable = false)
    private Integer ingredientId;

    // 발주품목번호 (FK, NULL 허용 - 판매/이월은 발주와 무관)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    // 로그 유형 (STOCK_IN / STOCK_OUT / STOCK_FORWARD / STOCK_DISCARD)
    @Column(name = "log_type", length = 50, nullable = false)
    private String logType;

    // 로그 메시지
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    // 발생 수량 (입고 +, 출고/폐기 -)
    @Column(name = "amount", nullable = false)
    private Integer amount;

    // 변동 사유
    @Column(name = "reason", length = 255, nullable = false)
    private String reason;

    // 수행자 (기본 SYSTEM)
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    // 등록일시 (insert 시 자동, 수정 불가)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
