package com.example.CafeAutoSystem.vendoringredient.service;

import com.example.CafeAutoSystem.common.entity.VendorIngredientEntity;
import com.example.CafeAutoSystem.common.repository.VendorIngredientRepository;
import com.example.CafeAutoSystem.vendoringredient.dto.VendorIngredientDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VendorIngredientService {

    private final VendorIngredientRepository vendorIngredientRepository;

    // 특정 식자재의 거래처 1/2/3순위 목록 조회
    public List<VendorIngredientDto> getByIngredient(Integer ingredientId) {
        return vendorIngredientRepository
                .findByIngredient_IngredientIdOrderByPriorityRankAsc(ingredientId)
                .stream()
                .map(VendorIngredientEntity::toDto)
                .toList();
    }

    // 거래쳐별 식자재 순위 계산 로직


}
