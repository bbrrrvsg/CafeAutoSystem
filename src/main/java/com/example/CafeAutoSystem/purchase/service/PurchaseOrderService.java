package com.example.CafeAutoSystem.purchase.service;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import com.example.CafeAutoSystem.ai_rpa.service.RpaExcelService;
import com.example.CafeAutoSystem.ai_rpa.service.RpaMailService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorIngredientRepository vendorIngredientRepository;
    private final CurrentStockLogRepository currentStockLogRepository;
    private final RpaExcelService rpaExcelService;
    private final RpaMailService rpaMailService;


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

    // 단건 조회
    public PurchaseOrderDto getById(Integer orderItemId) {
        PurchaseOrderEntity order = purchaseOrderRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("발주서를 찾을 수 없습니다. id=" + orderItemId));
        return order.toDto();
    }

    // 생성 (PENDING 발주서 신규 등록)
    //   수량(suggestedQty/finalQty) 단위는 식자재(ingredient.unit) 기준 — 프론트가 그 단위로 입력
    public PurchaseOrderDto createOrder(PurchaseOrderDto dto) {
        if (dto.getVendorIngredientId() == null) {
            throw new IllegalArgumentException("거래처-식자재 매핑(vendorIngredientId)은 필수입니다.");
        }
        VendorIngredientEntity vi = vendorIngredientRepository.findById(dto.getVendorIngredientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "거래처 매핑을 찾을 수 없습니다. id=" + dto.getVendorIngredientId()));

        Integer suggested = dto.getSuggestedQty();
        if (suggested == null || suggested <= 0) {
            throw new IllegalArgumentException("발주 수량(suggestedQty)은 1 이상이어야 합니다.");
        }
        Integer finalQty = (dto.getFinalQty() != null) ? dto.getFinalQty() : suggested;

        PurchaseOrderEntity order = PurchaseOrderEntity.builder()
                .vendorIngredient(vi)
                .orderDateKey("PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .suggestedQty(suggested)
                .finalQty(finalQty)
                .status("PENDING")
                .expirationDate(dto.getExpirationDate())
                .build();

        return purchaseOrderRepository.save(order).toDto();
    }

    // 삭제
    public void deleteOrder(Integer orderItemId) {
        if (!purchaseOrderRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("발주서를 찾을 수 없습니다. id=" + orderItemId);
        }
        purchaseOrderRepository.deleteById(orderItemId);
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

    // 승인 (PENDING -> COMPLETED)
    public PurchaseOrderDto approve(Integer orderItemId, String password) {
        verifyManagerPassword(password);
        PurchaseOrderEntity order = getPendingOrderOrThrow(orderItemId);
        order.setStatus("COMPLETED");

        if (order.getStatus().equals("COMPLETED")) {
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
        if(order.getStatus().equals("REJECTED")){
            rejectLog(order);
        }
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

    // 발주 로그 — 발주량(발주단위)을 재고단위로 환산해 STOCK_IN 기록
    public void purchaseLog(PurchaseOrderEntity order) {
        IngredientEntity ing = order.getVendorIngredient().getIngredient();
        int factor = ing.unitPerOrderOrDefault();      // 1 발주단위 = factor 재고단위
        int amount = order.getFinalQty() * factor;      // 예: 우유 15팩 × 1000 = 15000ml
        currentStockLogRepository.save(CurrentStockLogEntity.builder()
                .ingredient(ing)                       // @ManyToOne → 엔티티 그대로
                .orderItemId(order.getOrderItemId())
                .logType("STOCK_IN")
                .amount(amount)                        // 재고단위(ml/g/개)로 저장 → recipe/재고와 일관
                .reason("정기 발주 입고")
                .message("[입고] " + ing.getIngredientName() + " " + order.getFinalQty() + ing.orderUnitOrDefault()
                        + " 입고 (" + amount + ing.getUnit() + ")")
                .userId("SYSTEM")
                .build());
    }
    // 반려 로그 (입고와 별개)
    public void rejectLog(PurchaseOrderEntity order) {
        IngredientEntity ing = order.getVendorIngredient().getIngredient();
        currentStockLogRepository.save(CurrentStockLogEntity.builder()
                .ingredient(ing)
                .orderItemId(order.getOrderItemId())
                .logType("STOCK_REJECT")              // 반려 전용 타입
                .amount(0)
                .reason("발주 반려")
                .message("[반려] " + ing.getIngredientName() + " 발주 반려")
                .userId("SYSTEM")
                .build());
    }

    public void createBulkOrdersFromAi(List<PurchaseOrderDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new IllegalArgumentException("발주할 항목이 존재하지 않습니다.");
        }

        // 오늘 날짜 기준 발주 공통 키 생성 (예: PO-20260609)
        String dateKey = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (PurchaseOrderDto dto : dtoList) {
            // AI 추천 발주 수량이 없거나 0개 이하인 자재는 장부에 넣지 않고 패스
            if (dto.getSuggestedQty() == null || dto.getSuggestedQty() <= 0) {
                continue;
            }

            if (dto.getIngredientId() == null) {
                throw new IllegalArgumentException("자재 ID(ingredientId)는 필수 항목입니다.");
            }

            VendorIngredientEntity preferredVendorIngredient = vendorIngredientRepository
                    .findFirstByIngredient_IngredientIdOrderByPriorityRankAsc(dto.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "해당 식자재의 거래처 매핑을 찾을 수 없습니다. 자재 ID: " + dto.getIngredientId()));

            // 엔티티 조립 및 PENDING(대기) 상태 일괄 저장
            PurchaseOrderEntity orderEntity = PurchaseOrderEntity.builder()
                    .vendorIngredient(preferredVendorIngredient)
                    .orderDateKey(dateKey)
                    .suggestedQty(dto.getSuggestedQty())
                    .finalQty(dto.getSuggestedQty()) // 초기 검토 수량은 AI 제안 수량으로 동기화
                    .status("PENDING")
                    .expirationDate(dto.getExpirationDate())
                    .build();

            purchaseOrderRepository.save(orderEntity);
        }
    }
}
