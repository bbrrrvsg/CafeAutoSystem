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
                .map(this::toDto)
                .toList();
    }

    private VendorIngredientDto toDto(VendorIngredientEntity vi) {
        return VendorIngredientDto.builder()
                .vendorIngredientId(vi.getVendorIngredientId())
                .vendorId(vi.getVendor() != null ? vi.getVendor().getVendorId() : null)
                .vendorName(vi.getVendor() != null ? vi.getVendor().getVendorName() : null)
                .ingredientId(vi.getIngredient() != null ? vi.getIngredient().getIngredientId().intValue() : null)
                .ingredientName(vi.getIngredient() != null ? vi.getIngredient().getIngredientName() : null)
                .unitPrice(vi.getUnitPrice())
                .priorityRank(vi.getPriorityRank())
                .build();
    }
}
