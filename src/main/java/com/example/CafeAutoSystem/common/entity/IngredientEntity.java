package com.example.CafeAutoSystem.common.entity;

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
@Table(name = "ingredient")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IngredientEntity {

    // 식자재번호 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer ingredientId;

    // 식자재명
    @Column(name = "ingredient_name", length = 100, nullable = false)
    private String ingredientName;

    // 관리단위 (g, 개, pack, ml ...)
    @Column(name = "unit", length = 20, nullable = false)
    private String unit;

    // 안전재고 기준치
    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    // 물품 이미지 경로
    @Column(name = "ingredient_image", length = 255, nullable = false)
    private String ingredientImage;
}
