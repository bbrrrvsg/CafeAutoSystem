package com.example.CafeAutoSystem.common.entity;

import com.example.CafeAutoSystem.purchase.dto.PurchaseOrderDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_order")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PurchaseOrderEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    // 거래처별 식자재 매핑 (N:1) — FK: vendor_ingredient_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_ingredient_id", nullable = false)
    @ToString.Exclude
    private VendorIngredientEntity vendorIngredient;

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

    // -----------------------------------------------------
    // 엔티티 → DTO (매핑 체인 타고 거래처/식자재 정보까지 채움)
    // -----------------------------------------------------
    public PurchaseOrderDto toDto() {
        PurchaseOrderDto.PurchaseOrderDtoBuilder b = PurchaseOrderDto.builder()
                .orderItemId(this.orderItemId)
                .orderDateKey(this.orderDateKey)
                .suggestedQty(this.suggestedQty)
                .finalQty(this.finalQty)
                .status(this.status)
                .expirationDate(this.expirationDate)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt());

        // 매핑 정보 보강 (LAZY → @Transactional 안에서 호출 필요)
        if (this.vendorIngredient != null) {
            b.vendorIngredientId(this.vendorIngredient.getVendorIngredientId());
            b.unitPrice(this.vendorIngredient.getUnitPrice());
            b.priorityRank(this.vendorIngredient.getPriorityRank());

            if (this.vendorIngredient.getVendor() != null) {
                b.vendorId(this.vendorIngredient.getVendor().getVendorId());
                b.vendorName(this.vendorIngredient.getVendor().getVendorName());
            }
            if (this.vendorIngredient.getIngredient() != null) {
                b.ingredientId(this.vendorIngredient.getIngredient().getIngredientId().intValue());
                b.ingredientName(this.vendorIngredient.getIngredient().getIngredientName());
                b.ingredientUnit(this.vendorIngredient.getIngredient().getUnit());               // 재고 단위
                b.orderUnit(this.vendorIngredient.getIngredient().orderUnitOrDefault());         // 발주 단위
            }
        }
        return b.build();
    }
}
