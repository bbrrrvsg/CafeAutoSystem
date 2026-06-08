package com.example.CafeAutoSystem.common.entity;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.menurecipe.dto.MenuRecipeDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MENU_RECIPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuRecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    /** 메뉴 1개 기준 재료 소요량 */
    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity;

    @Column(name = "note", length = 100)
    private String note;

    /** 주문 수량에 따른 총 소요량 계산 */
    public int calcTotalQty(int orderQuantity) {
        return this.requiredQuantity * orderQuantity;
    }

    // 엔티티 → DTO (ingredient 는 LAZY → @Transactional 안에서 호출)
    public MenuRecipeDto toDto() {
        MenuRecipeDto.MenuRecipeDtoBuilder b = MenuRecipeDto.builder()
                .recipeId(this.recipeId)
                .menuName(this.menuName)
                .price(this.price)
                .requiredQuantity(this.requiredQuantity)
                .note(this.note);
        if (this.ingredient != null) {
            b.ingredientId(this.ingredient.getIngredientId());
            b.ingredientName(this.ingredient.getIngredientName());
            b.ingredientUnit(this.ingredient.getUnit());
        }
        return b.build();
    }
}
