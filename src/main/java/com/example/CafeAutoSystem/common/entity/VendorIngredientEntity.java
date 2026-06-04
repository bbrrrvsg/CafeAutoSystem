package com.example.CafeAutoSystem.common.entity;

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

import com.example.CafeAutoSystem.vendoringredient.dto.VendorIngredientDto;

@Entity
@Table(name = "vendor_ingredient")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VendorIngredientEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_ingredient_id")
    private Integer vendorIngredientId;

    // 거래처 (N:1) — FK: vendor_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @ToString.Exclude
    private VendorEntity vendor;

    // 식자재 (N:1) — FK: ingredient_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @ToString.Exclude
    private IngredientEntity ingredient;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "priority_rank", nullable = false)
    private Integer priorityRank;

    // 엔티티 → DTO (vendor/ingredient 가 LAZY 라 @Transactional 안에서 호출)
    public VendorIngredientDto toDto() {
        return VendorIngredientDto.builder()
                .vendorIngredientId(this.vendorIngredientId)
                .vendorId(this.vendor != null ? this.vendor.getVendorId() : null)
                .vendorName(this.vendor != null ? this.vendor.getVendorName() : null)
                .ingredientId(this.ingredient != null ? this.ingredient.getIngredientId().intValue() : null)
                .ingredientName(this.ingredient != null ? this.ingredient.getIngredientName() : null)
                .unitPrice(this.unitPrice)
                .priorityRank(this.priorityRank)
                .build();
    }
}
