package com.example.CafeAutoSystem.vendoringredient.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
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

@Entity
@Table(name = "vendor_ingredient")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VendorIngredient extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_ingredient_id")
    private Integer vendorIngredientId;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(name = "ingredient_id", nullable = false)
    private Integer ingredientId;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "priority_rank", nullable = false)
    private Integer priorityRank;
}
