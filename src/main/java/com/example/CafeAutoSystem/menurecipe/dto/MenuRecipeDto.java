package com.example.CafeAutoSystem.menurecipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MenuRecipeDto {

    private Long    recipeId;
    private String  menuName;          // 메뉴명
    private Integer price;             // 메뉴 가격
    private Integer ingredientId;      // 재료 FK (입력/출력)
    private String  ingredientName;    // 보강 (toDto 가 채움)
    private String  ingredientUnit;    // 보강 — requiredQuantity 의 단위(g/ml/개 ...)
    private Integer requiredQuantity;  // 메뉴 1개당 재료 소요량 (ingredientUnit 기준)
    private String  note;              // 비고 (선택)
}
