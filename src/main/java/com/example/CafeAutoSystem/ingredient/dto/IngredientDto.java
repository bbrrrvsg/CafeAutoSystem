package com.example.CafeAutoSystem.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IngredientDto {

    private Integer ingredientId;
    private String  ingredientName;   // 식자재명
    private String  unit;             // 관리 단위 (g, ml, 개, pack ...) — 발주/재고 수량의 기준
    private Integer safetyStock;      // 안전재고 기준치
    private String  ingredientImage;  // 이미지 경로 (선택)
}
