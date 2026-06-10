package com.example.CafeAutoSystem.ingredient.service;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    // 목록
    @Transactional(readOnly = true)
    public List<IngredientDto> getAll() {
        return ingredientRepository.findAll().stream()
                .map(IngredientEntity::toDto)
                .toList();
    }

    // 단건
    @Transactional(readOnly = true)
    public IngredientDto getById(Integer ingredientId) {
        return findOrThrow(ingredientId).toDto();
    }

    // 생성
    public IngredientDto create(IngredientDto dto) {
        validate(dto);
        IngredientEntity entity = IngredientEntity.builder()
                .ingredientName(dto.getIngredientName())
                .unit(dto.getUnit())
                .safetyStock(dto.getSafetyStock())
                .ingredientImage(dto.getIngredientImage())
                .build();
        return ingredientRepository.save(entity).toDto();
    }

    // 수정 (null 필드는 미변경)
    public IngredientDto update(Integer ingredientId, IngredientDto dto) {
        IngredientEntity entity = findOrThrow(ingredientId);
        if (dto.getIngredientName() != null) entity.setIngredientName(dto.getIngredientName());
        if (dto.getUnit() != null)           entity.setUnit(dto.getUnit());
        if (dto.getSafetyStock() != null)    entity.setSafetyStock(dto.getSafetyStock());
        if (dto.getIngredientImage() != null) entity.setIngredientImage(dto.getIngredientImage());
        return entity.toDto(); // @Transactional → dirty checking 으로 자동 UPDATE
    }

    // 삭제
    public void delete(Integer ingredientId) {
        if (!ingredientRepository.existsById(ingredientId)) {
            throw new IllegalArgumentException("식자재를 찾을 수 없습니다. id=" + ingredientId);
        }
        ingredientRepository.deleteById(ingredientId);
    }

    // ----- 내부 -----
    private IngredientEntity findOrThrow(Integer ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("식자재를 찾을 수 없습니다. id=" + ingredientId));
    }

    private void validate(IngredientDto dto) {
        if (dto.getIngredientName() == null || dto.getIngredientName().isBlank()) {
            throw new IllegalArgumentException("식자재명(ingredientName)은 필수입니다.");
        }
        if (dto.getUnit() == null || dto.getUnit().isBlank()) {
            throw new IllegalArgumentException("단위(unit)는 필수입니다.");
        }
        if (!java.util.Set.of("ml", "g", "개", "pack").contains(dto.getUnit())) {
            throw new IllegalArgumentException("단위(unit)는 ml, g, 개, pack 중 하나여야 합니다.");
        }
        if (dto.getSafetyStock() == null || dto.getSafetyStock() < 0) {
            throw new IllegalArgumentException("안전재고(safetyStock)는 0 이상이어야 합니다.");
        }
    }
}
