package com.example.CafeAutoSystem.jms_ai_rpa.entity;

import com.example.CafeAutoSystem.global.config.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "PURCHASE_ORDER")
public class PurchaseOrderEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId; // 발주품목번호 (PK)

    @Column(name = "order_date_key", nullable = false, length = 50)
    private String orderDateKey; // 발주날짜키 (Group 묶음용)

    @Column(name = "vendor_ingredient_id", nullable = false)
    private Integer vendorIngredientId; // 거래처별제품매핑번호 (FK)

    @Column(name = "suggested_qty", nullable = false)
    private Integer suggestedQty; // AI 발주 제안 수량

    @Column(name = "final_qty", nullable = false)
    private Integer finalQty; // 사장님 최종 확정 수량

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // 발주상태 (기본값 PENDING)

    @Column(name = "expiration_date")
    private LocalDate expirationDate; // 입고 시 확정될 유통기한
}
