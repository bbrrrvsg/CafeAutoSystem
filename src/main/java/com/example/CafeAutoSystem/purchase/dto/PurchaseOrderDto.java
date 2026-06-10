package com.example.CafeAutoSystem.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PurchaseOrderDto {

    // ===== PURCHASE_ORDER 컬럼 =====
    private Integer orderItemId;
    private Integer vendorIngredientId;
    private String  orderDateKey;
    private Integer suggestedQty;
    private Integer finalQty;
    private String  status;
    private LocalDate expirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== 매핑 체인 보강 필드 (DB 컬럼 아님, toDto 가 채움) =====
    private Integer vendorId;
    private String  vendorName;       // vendor.vendor_name
    private Integer ingredientId;
    private String  ingredientName;   // ingredient.ingredient_name
    private String  ingredientUnit;   // ingredient.unit (단위 ml/g/개)
    private Integer unitPrice;        // vendor_ingredient.unit_price (단가)
    private Integer priorityRank;     // vendor_ingredient.priority_rank
}
