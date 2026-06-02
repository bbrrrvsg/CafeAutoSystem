package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Integer> {
    List<PurchaseOrderEntity> findByStatus(String status);
}
