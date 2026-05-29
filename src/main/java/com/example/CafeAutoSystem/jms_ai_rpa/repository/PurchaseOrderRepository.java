package com.example.CafeAutoSystem.jms_ai_rpa.repository;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Integer> {
    // 사장님 승인 대기중(PENDING)이거나 완료(COMPLETED)된 발주서 상태별 필터링 조회 시 사용
    List<PurchaseOrderEntity> findByStatus(String status);

    // 특정 날짜 그룹키(예: PO-20260529)에 묶인 발주 품목들을 싹 긁어올 때 사용 (RPA 엑셀 가공용)
    List<PurchaseOrderEntity> findByOrderDateKey(String orderDateKey);
}
