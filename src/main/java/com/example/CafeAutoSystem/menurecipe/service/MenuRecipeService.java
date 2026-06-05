package com.example.CafeAutoSystem.menurecipe.service;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.common.repository.MenuRecipeRepository;
import com.example.CafeAutoSystem.menurecipe.dto.MenuRecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuRecipeService {

    private final MenuRecipeRepository menuRecipeRepository;
    private final IngredientRepository ingredientRepository;

    // 목록
    @Transactional(readOnly = true)
    public List<MenuRecipeDto> getAll() {
        return menuRecipeRepository.findAll().stream()
                .map(MenuRecipeEntity::toDto)
                .toList();
    }

    // 단건
    @Transactional(readOnly = true)
    public MenuRecipeDto getById(Long recipeId) {
        return findOrThrow(recipeId).toDto();
    }

    // 메뉴명으로 레시피 조회 (재고 차감 로직에서 쓰는 그 조회)
    @Transactional(readOnly = true)
    public List<MenuRecipeDto> getByMenuName(String menuName) {
        return menuRecipeRepository.findByMenuNameWithIngredient(menuName).stream()
                .map(MenuRecipeEntity::toDto)
                .toList();
    }

    // 생성
    public MenuRecipeDto create(MenuRecipeDto dto) {
        validate(dto);
        IngredientEntity ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("식자재를 찾을 수 없습니다. id=" + dto.getIngredientId()));
        MenuRecipeEntity entity = MenuRecipeEntity.builder()
                .menuName(dto.getMenuName())
                .price(dto.getPrice())
                .ingredient(ingredient)
                .requiredQuantity(dto.getRequiredQuantity())
                .note(dto.getNote())
                .build();
        return menuRecipeRepository.save(entity).toDto();
    }

    // 수정 (null 필드는 미변경)
    public MenuRecipeDto update(Long recipeId, MenuRecipeDto dto) {
        MenuRecipeEntity entity = findOrThrow(recipeId);
        if (dto.getMenuName() != null)         entity.setMenuName(dto.getMenuName());
        if (dto.getPrice() != null)            entity.setPrice(dto.getPrice());
        if (dto.getRequiredQuantity() != null) entity.setRequiredQuantity(dto.getRequiredQuantity());
        if (dto.getNote() != null)             entity.setNote(dto.getNote());
        if (dto.getIngredientId() != null) {
            IngredientEntity ingredient = ingredientRepository.findById(dto.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("식자재를 찾을 수 없습니다. id=" + dto.getIngredientId()));
            entity.setIngredient(ingredient);
        }
        return entity.toDto(); // dirty checking
    }

    // 삭제
    public void delete(Long recipeId) {
        if (!menuRecipeRepository.existsById(recipeId)) {
            throw new IllegalArgumentException("레시피를 찾을 수 없습니다. id=" + recipeId);
        }
        menuRecipeRepository.deleteById(recipeId);
    }

    // ----- 내부 -----
    private MenuRecipeEntity findOrThrow(Long recipeId) {
        return menuRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다. id=" + recipeId));
    }

    private void validate(MenuRecipeDto dto) {
        if (dto.getMenuName() == null || dto.getMenuName().isBlank()) {
            throw new IllegalArgumentException("메뉴명(menuName)은 필수입니다.");
        }
        if (dto.getIngredientId() == null) {
            throw new IllegalArgumentException("식자재(ingredientId)는 필수입니다.");
        }
        if (dto.getPrice() == null || dto.getPrice() < 0) {
            throw new IllegalArgumentException("가격(price)은 0 이상이어야 합니다.");
        }
        if (dto.getRequiredQuantity() == null || dto.getRequiredQuantity() <= 0) {
            throw new IllegalArgumentException("소요량(requiredQuantity)은 1 이상이어야 합니다.");
        }
    }
}
