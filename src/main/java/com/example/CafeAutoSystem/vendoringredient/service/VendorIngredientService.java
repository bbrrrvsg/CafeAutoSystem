package com.example.CafeAutoSystem.vendoringredient.service;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.VendorEntity;
import com.example.CafeAutoSystem.common.entity.VendorIngredientEntity;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.common.repository.VendorIngredientRepository;
import com.example.CafeAutoSystem.common.repository.VendorRepository;
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
    private final VendorRepository vendorRepository;
    private final IngredientRepository ingredientRepository;

    // 전체 목록
    public List<VendorIngredientDto> getAll() {
        return vendorIngredientRepository.findAll().stream()
                .map(VendorIngredientEntity::toDto)
                .toList();
    }

    // 단건
    public VendorIngredientDto getById(Integer vendorIngredientId) {
        return findOrThrow(vendorIngredientId).toDto();
    }

    // 특정 식자재의 거래처 1/2/3순위 목록 조회
    public List<VendorIngredientDto> getByIngredient(Integer ingredientId) {
        return vendorIngredientRepository
                .findByIngredient_IngredientIdOrderByPriorityRankAsc(ingredientId)
                .stream()
                .map(VendorIngredientEntity::toDto)
                .toList();
    }

    // 생성 — 등록 후 단가순 우선순위 자동 재계산
    public VendorIngredientDto create(VendorIngredientDto dto) {
        if (dto.getVendorId() == null || dto.getIngredientId() == null) {
            throw new IllegalArgumentException("vendorId, ingredientId는 필수입니다.");
        }
        if (dto.getUnitPrice() == null || dto.getUnitPrice() < 0) {
            throw new IllegalArgumentException("단가(unitPrice)는 0 이상이어야 합니다.");
        }
        VendorEntity vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("거래처를 찾을 수 없습니다. id=" + dto.getVendorId()));
        IngredientEntity ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("식자재를 찾을 수 없습니다. id=" + dto.getIngredientId()));

        VendorIngredientEntity entity = VendorIngredientEntity.builder()
                .vendor(vendor)
                .ingredient(ingredient)
                .unitPrice(dto.getUnitPrice())
                .priorityRank(0) // 임시값 — 아래 recalc 가 단가순으로 재부여
                .build();
        VendorIngredientEntity saved = vendorIngredientRepository.save(entity);

        recalculatePriority(dto.getIngredientId());
        return saved.toDto();
    }

    // 수정 — 단가 변경 시 재계산
    public VendorIngredientDto update(Integer vendorIngredientId, VendorIngredientDto dto) {
        VendorIngredientEntity entity = findOrThrow(vendorIngredientId);
        if (dto.getUnitPrice() != null) {
            if (dto.getUnitPrice() < 0) {
                throw new IllegalArgumentException("단가(unitPrice)는 0 이상이어야 합니다.");
            }
            entity.setUnitPrice(dto.getUnitPrice());
            recalculatePriority(entity.getIngredient().getIngredientId()); // 단가 바뀌면 재정렬
        }
        return entity.toDto();
    }

    // 삭제 — 삭제 후 순위 구멍 메움
    public void delete(Integer vendorIngredientId) {
        VendorIngredientEntity entity = findOrThrow(vendorIngredientId);
        Integer ingredientId = entity.getIngredient().getIngredientId();
        vendorIngredientRepository.delete(entity);
        recalculatePriority(ingredientId);
    }

    /**
     * 단가순 우선순위 재계산.
     * 같은 식자재의 거래처들을 unit_price 오름차순(싼 순, 동률이면 등록순 id)으로 정렬해
     * priority_rank 를 1,2,3... 다시 부여. @Transactional dirty checking 으로 자동 저장.
     */
    public void recalculatePriority(Integer ingredientId) {
        List<VendorIngredientEntity> list = vendorIngredientRepository
                .findByIngredient_IngredientIdOrderByUnitPriceAscVendorIngredientIdAsc(ingredientId);
        int rank = 1;
        for (VendorIngredientEntity vi : list) {
            vi.setPriorityRank(rank++);
        }
    }

    private VendorIngredientEntity findOrThrow(Integer vendorIngredientId) {
        return vendorIngredientRepository.findById(vendorIngredientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "거래처-식자재 매핑을 찾을 수 없습니다. id=" + vendorIngredientId));
    }
}
