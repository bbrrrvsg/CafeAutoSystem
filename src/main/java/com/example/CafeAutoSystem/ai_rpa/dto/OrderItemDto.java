package com.example.CafeAutoSystem.ai_rpa.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {
    private String ingredientName; // 자재명
    private int orderQty; // 발주 수량
    private int predictedRequiredQty; // 예상 필요량
    private int currentStock;         // 현재고
    private int unitPrice;            // 단가
    private int totalPrice;           // 예상 금액 (발주 제안량 * 단가)
    private Integer ingredientId;
}
