package com.example.CafeAutoSystem.jms_ai_rpa.entity;

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
    private Long ingredientId;

    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "ingredient_image", length = 255)
    private String ingredientImage;

    @JsonManagedReference
    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuRecipeEntity> recipes = new ArrayList<>();
}
