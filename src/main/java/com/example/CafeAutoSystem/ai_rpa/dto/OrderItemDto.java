package com.example.CafeAutoSystem.ai_rpa.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {
    private Integer ingredientId;     // 자재 ID
    private String ingredientName;    // 자재명
    private int orderQty;             // 발주 수량 (AI 제안 수량)
    private int predictedRequiredQty; // 예상 필요량 (3일 소모 예측)
    private int currentStock;         // 현재고
    private int unitPrice;            // 단가
    private int totalPrice;           // 예상 금액 (발주 수량 * 단가)
    private String ingredientUnit;
    private int safetyStock;
}