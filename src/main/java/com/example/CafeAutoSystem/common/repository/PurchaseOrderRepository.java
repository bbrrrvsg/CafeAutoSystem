package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Integer> {
    List<PurchaseOrderEntity> findByStatus(String status);

    @Query("""
        SELECT p FROM PurchaseOrderEntity p
        JOIN p.vendorIngredient vi
        JOIN vi.ingredient i
        JOIN vi.vendor v
        WHERE i.ingredientName LIKE %:keyword%
           OR v.vendorName LIKE %:keyword%
           OR p.status LIKE %:keyword%
    """)
    List<PurchaseOrderEntity> searchByKeyword(@Param("keyword") String keyword);
}
