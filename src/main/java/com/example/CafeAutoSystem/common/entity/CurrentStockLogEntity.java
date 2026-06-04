package com.example.CafeAutoSystem.common.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CURRENT_STOCK_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentStockLogEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    /**
     * 발주품목번호 FK (nullable)
     * STOCK_OUT(판매 차감) 시에는 null, STOCK_IN(입고) 시에는 order_item_id 입력
     */
    @Column(name = "order_item_id")
    private Integer orderItemId;

    /**
     * 로그 유형
     * STOCK_IN / STOCK_OUT / STOCK_FORWARD / STOCK_WARNING / AI_VALIDATION / USER_APPROVE / RPA_RETRY
     */
    @Column(name = "log_type", nullable = false, length = 50)
    private String logType;

    /**
     * 포맷된 로그 메시지
     * 예: "[판매] 카페 라떼 1잔 판매", "[입고] 신선한 우유 10팩 입고"
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * 발생 수량
     * STOCK_OUT은 음수(-200), STOCK_IN은 양수(10000), 이벤트성 로그는 0
     */
    @Column(name = "amount", nullable = false)
    private Integer amount;

    /** 변동 사유. 예: "레시피 자동 차감", "정기 발주 입고" */
    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    /** 수행자. 시스템 자동 처리는 "SYSTEM", 관리자는 "admin" 등 */
    @Column(name = "user_id", nullable = false, length = 50)
    @Builder.Default
    private String userId = "SYSTEM";
}
