package com.example.CafeAutoSystem.purchase.dto;

import com.example.CafeAutoSystem.purchase.entity.PurchaseOrder;
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

    private Integer orderItemId;
    private Integer vendorIngredientId;
    private String  orderDateKey;
    private Integer suggestedQty;
    private Integer finalQty;
    private String  status;
    private LocalDate expirationDate;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public PurchaseOrder toEntity() {
        return PurchaseOrder.builder()
                .orderItemId(this.orderItemId)
                .vendorIngredientId(this.vendorIngredientId)
                .orderDateKey(this.orderDateKey)
                .suggestedQty(this.suggestedQty)
                .finalQty(this.finalQty)
                .status(this.status)
                .expirationDate(this.expirationDate)
                .build();
    }
}
