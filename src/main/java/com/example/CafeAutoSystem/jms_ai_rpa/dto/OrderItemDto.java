package com.example.CafeAutoSystem.jms_ai_rpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderItemDto {
    private String ingredientName; // 자재명
    private int orderQty; // 발주 수량
}
