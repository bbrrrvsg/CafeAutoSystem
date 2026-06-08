package com.example.CafeAutoSystem.ai_rpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiPredictRequestDto {
    private Integer ingredientId;    // 분석할 재료 ID
    private String dayOfWeek;         // 현재 요일
    private int currentStock;        // 현재 실재고 수량
    private int safetyStock;         // 안전 재고 수량
    private List<Integer> rawAmounts; // 순수 소모량 리스트
}
