package com.example.CafeAutoSystem.purchase.repository;

import com.example.CafeAutoSystem.purchase.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    // JpaRepository 기본 제공: save / findById / findAll / deleteById / count ...

    // 발주 상태별 조회 (예: "PENDING", "COMPLETED", "REJECTED")
    //   Spring Data JPA 가 메서드 이름으로 자동 쿼리 생성
    //   → SELECT * FROM purchase_order WHERE status = ?
    List<PurchaseOrder> findByStatus(String status);

}
