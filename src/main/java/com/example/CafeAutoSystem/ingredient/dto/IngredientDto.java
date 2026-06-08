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
    private String  orderUnit;        // 발주 단위명 (팩/kg/개 ...) — null이면 재고단위(unit)와 동일
    private Integer unitPerOrder;     // 1 발주단위 = N 재고단위 (예: 우유 1팩=1000ml → 1000)
}
