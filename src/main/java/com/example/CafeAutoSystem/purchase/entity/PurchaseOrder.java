package com.example.CafeAutoSystem.purchase.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import com.example.CafeAutoSystem.purchase.dto.PurchaseOrderDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_order")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PurchaseOrder extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Column(name = "vendor_ingredient_id", nullable = false)
    private Integer vendorIngredientId;

    @Column(name = "order_date_key", length = 50, nullable = false)
    private String orderDateKey;

    @Column(name = "suggested_qty", nullable = false)
    private Integer suggestedQty;

    @Column(name = "final_qty", nullable = false)
    private Integer finalQty;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    public PurchaseOrderDto toDto() {
        return PurchaseOrderDto.builder()
                .orderItemId(this.orderItemId)
                .vendorIngredientId(this.vendorIngredientId)
                .orderDateKey(this.orderDateKey)
                .suggestedQty(this.suggestedQty)
                .finalQty(this.finalQty)
                .status(this.status)
                .expirationDate(this.expirationDate)
                .createDate(getCreateDate())
                .updateDate(getUpdateDate())
                .build();
    }
}
