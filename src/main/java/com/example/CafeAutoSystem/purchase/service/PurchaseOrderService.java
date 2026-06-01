package com.example.CafeAutoSystem.purchase.service;

import com.example.CafeAutoSystem.purchase.dto.PurchaseOrderDto;
import com.example.CafeAutoSystem.purchase.entity.PurchaseOrder;
import com.example.CafeAutoSystem.purchase.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Value("${cafe.manager.password}")
    private String managerPassword;

    // 상태별 목록
    public List<PurchaseOrderDto> getPendingList()   { return findByStatusAsDto("PENDING");   }
    public List<PurchaseOrderDto> getCompletedList() { return findByStatusAsDto("COMPLETED"); }
    public List<PurchaseOrderDto> getRejectedList()  { return findByStatusAsDto("REJECTED");  }

    private List<PurchaseOrderDto> findByStatusAsDto(String status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(PurchaseOrder::toDto)
                .toList();
    }

    // 수정 (수량 / vendor_ingredient_id)
    public PurchaseOrderDto updateOrder(Integer orderItemId, PurchaseOrderDto dto, String password) {
        verifyManagerPassword(password);
        PurchaseOrder order = getPendingOrderOrThrow(orderItemId);

        order.setVendorIngredientId(dto.getVendorIngredientId());
        order.setSuggestedQty(dto.getSuggestedQty());
        order.setFinalQty(dto.getFinalQty());

        return order.toDto();
    }

    // 승인 (PENDING → COMPLETED)
    public PurchaseOrderDto approve(Integer orderItemId, String password) {
        verifyManagerPassword(password);
        PurchaseOrder order = getPendingOrderOrThrow(orderItemId);
        order.setStatus("COMPLETED");
        return order.toDto();
    }

    // 반려 (PENDING → REJECTED, final_qty=0)
    public PurchaseOrderDto reject(Integer orderItemId, String password) {
        verifyManagerPassword(password);
        PurchaseOrder order = getPendingOrderOrThrow(orderItemId);
        order.setStatus("REJECTED");
        order.setFinalQty(0);
        return order.toDto();
    }

    private PurchaseOrder getPendingOrderOrThrow(Integer orderItemId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("발주서를 찾을 수 없습니다. id=" + orderItemId));
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("PENDING 상태의 발주서만 처리할 수 있습니다. 현재 상태=" + order.getStatus());
        }
        return order;
    }

    private void verifyManagerPassword(String password) {
        if (!managerPassword.equals(password)) {
            throw new IllegalArgumentException("점장 비밀번호가 일치하지 않습니다.");
        }
    }
}
