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
}
