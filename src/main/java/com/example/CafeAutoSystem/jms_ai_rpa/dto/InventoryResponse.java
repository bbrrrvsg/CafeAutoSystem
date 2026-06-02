package com.example.CafeAutoSystem.jms_ai_rpa.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long ingredientId;  // 재료ID
    private String ingredientName;  // 재료 이름
    private String unit;        // 단위
    private int currentStock;   // CURRENT_STOCK_LOG SUM(amount) 현재 재고
    private int safetyStock;    // 안전 재고
    private String ingredientImage; // 재료 사진

    /**
     * 상태
     * LOW  : currentStock <= safetyStock
     * WARN : currentStock <= safetyStock * 1.5
     * OK   : 그 외 정상
     */
    private String status;

    /** 재고 비율 (프로그레스바용, 0~100) */
    private int stockPercent;
}
