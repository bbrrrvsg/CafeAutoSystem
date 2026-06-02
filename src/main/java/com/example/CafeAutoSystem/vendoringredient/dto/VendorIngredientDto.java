package com.example.CafeAutoSystem.vendoringredient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VendorIngredientDto {
    private Integer vendorIngredientId;
    private Integer vendorId;
    private String  vendorName;
    private Integer ingredientId;
    private String  ingredientName;
    private Integer unitPrice;
    private Integer priorityRank;
}
