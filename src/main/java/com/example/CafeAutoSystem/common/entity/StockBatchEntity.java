package com.example.CafeAutoSystem.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * FEFO(First Expired, First Out) 배치 관리 엔티티
 *
 * 발주 승인 시 생성. 판매 차감 시 expiryDate ASC 순으로 remaining_qty 차감.
 * 폐기 스케줄러(23:00)가 expiryDate <= 오늘 인 배치를 자동 폐기.
 */
@Entity
@Table(name = "stock_batch")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBatchEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    /** 재료 FK */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    /** 연결된 발주서 ID (nullable - 초기 적재 배치는 null) */
    @Column(name = "order_item_id")
    private Integer orderItemId;

    /** 최초 입고 수량 */
    @Column(name = "received_qty", nullable = false)
    private Integer receivedQty;

    /** 남은 수량 (판매 차감 시 감소, 0이 되면 소진) */
    @Column(name = "remaining_qty", nullable = false)
    private Integer remainingQty;

    /** 유통기한 (FEFO 정렬 기준) */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}
