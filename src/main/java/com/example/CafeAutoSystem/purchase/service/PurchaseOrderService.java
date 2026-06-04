package com.example.CafeAutoSystem.purchase.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.common.entity.VendorIngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.PurchaseOrderRepository;
import com.example.CafeAutoSystem.common.repository.VendorIngredientRepository;
import com.example.CafeAutoSystem.purchase.dto.PurchaseOrderDto;
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
    private final VendorIngredientRepository vendorIngredientRepository;
    private final CurrentStockLogRepository currentStockLogRepository;


    @Value("${cafe.manager.password}")
    private String managerPassword;

    // 상태별 목록
    public List<PurchaseOrderDto> getPendingList()   { return findByStatusAsDto("PENDING");   }
    public List<PurchaseOrderDto> getCompletedList() { return findByStatusAsDto("COMPLETED"); }
    public List<PurchaseOrderDto> getRejectedList()  { return findByStatusAsDto("REJECTED");  }

    private List<PurchaseOrderDto> findByStatusAsDto(String status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(PurchaseOrderEntity::toDto)
                .toList();
    }

    // 수정 (수량 / 거래처매핑 변경)
    public PurchaseOrderDto updateOrder(Integer orderItemId, PurchaseOrderDto dto, String password) {
        verifyManagerPassword(password);
        PurchaseOrderEntity order = getPendingOrderOrThrow(orderItemId);

        // 거래처 매핑 변경 (1/2/3순위 우회 선택)
        if (dto.getVendorIngredientId() != null) {
            VendorIngredientEntity vi = vendorIngredientRepository.findById(dto.getVendorIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "거래처 매핑을 찾을 수 없습니다. id=" + dto.getVendorIngredientId()));
            order.setVendorIngredient(vi);
        }
        order.setSuggestedQty(dto.getSuggestedQty());
        order.setFinalQty(dto.getFinalQty());

        return order.toDto();
    }

    // 승인 (PENDING → COMPLETED)
    public PurchaseOrderDto approve(Integer orderItemId, String password) {
        verifyManagerPassword(password);
        PurchaseOrderEntity order = getPendingOrderOrThrow(orderItemId);
        order.setStatus("COMPLETED");
        // 승인시 로그 작성을 위해 발주 엔티티 전달
        if(order.getStatus().equals("COMPLETED")){
            purchaseLog(order);
        }
        return order.toDto();
    }

    // 반려 (PENDING → REJECTED, final_qty=0)
    public PurchaseOrderDto reject(Integer orderItemId, String password) {
        verifyManagerPassword(password);
        PurchaseOrderEntity order = getPendingOrderOrThrow(orderItemId);
        order.setStatus("REJECTED");
        order.setFinalQty(0);
        return order.toDto();
    }

    private PurchaseOrderEntity getPendingOrderOrThrow(Integer orderItemId) {
        PurchaseOrderEntity order = purchaseOrderRepository.findById(orderItemId)
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

    // 발주 로그
    public void purchaseLog(PurchaseOrderEntity order) {
        IngredientEntity ing = order.getVendorIngredient().getIngredient();
        currentStockLogRepository.save(CurrentStockLogEntity.builder()
                .ingredient(ing)                       // @ManyToOne → 엔티티 그대로
                .orderItemId(order.getOrderItemId())
                .logType("STOCK_IN")
                .amount(order.getFinalQty())
                .reason("정기 발주 입고")
                .message("[입고] " + ing.getIngredientName() + " " + order.getFinalQty() + "개 입고")
                .userId("SYSTEM")
                .build());
    }
}
