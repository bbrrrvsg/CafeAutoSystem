package com.example.CafeAutoSystem.common.entity;

import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INGREDIENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer ingredientId;

    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "ingredient_image", length = 255)
    private String ingredientImage;

    /** 발주(구매) 단위명. 예: 팩, kg, 개. null이면 재고단위(unit)와 동일하게 취급 */
    @Column(name = "order_unit", length = 20)
    private String orderUnit;

    /** 1 발주단위 = N 재고단위(unit). 예: 우유 1팩 = 1000ml → 1000. null이면 1(환산 없음) */
    @Column(name = "unit_per_order")
    private Integer unitPerOrder;

    @JsonManagedReference
    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuRecipeEntity> recipes = new ArrayList<>();

    /** 1 발주단위당 재고단위 수 (null/0이면 1) */
    public int unitPerOrderOrDefault() { return (unitPerOrder != null && unitPerOrder > 0) ? unitPerOrder : 1; }
    /** 발주 단위명 (null이면 재고단위 unit) */
    public String orderUnitOrDefault() { return (orderUnit != null && !orderUnit.isBlank()) ? orderUnit : unit; }

    // 엔티티 → DTO (recipes 는 LAZY/순환 방지 위해 제외)
    public IngredientDto toDto() {
        return IngredientDto.builder()
                .ingredientId(this.ingredientId)
                .ingredientName(this.ingredientName)
                .unit(this.unit)
                .safetyStock(this.safetyStock)
                .ingredientImage(this.ingredientImage)
                .orderUnit(this.orderUnit)
                .unitPerOrder(this.unitPerOrder)
                .build();
    }
}
