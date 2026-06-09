package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.VendorIngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorIngredientRepository extends JpaRepository<VendorIngredientEntity, Integer> {

    // 특정 식자재의 거래처 매핑을 우선순위 오름차순 (1순위 → 2순위 → 3순위)
    List<VendorIngredientEntity> findByIngredient_IngredientIdOrderByPriorityRankAsc(Integer ingredientId);

    // 단가 오름차순(동률 시 등록순 id) — recalculatePriority 재정렬용
    List<VendorIngredientEntity> findByIngredient_IngredientIdOrderByUnitPriceAscVendorIngredientIdAsc(Integer ingredientId);

    Optional<VendorIngredientEntity> findFirstByIngredient_IngredientIdOrderByPriorityRankAsc(Integer ingredientId);
}
