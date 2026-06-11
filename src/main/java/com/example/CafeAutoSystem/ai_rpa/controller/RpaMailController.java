package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import com.example.CafeAutoSystem.ai_rpa.service.RpaExcelService;
import com.example.CafeAutoSystem.ai_rpa.service.RpaMailService;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.common.repository.PurchaseOrderRepository; // 🌟 주입 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/jms-rpa")
@RequiredArgsConstructor
public class RpaMailController {

    private final RpaMailService rpaMailService;
    private final RpaExcelService rpaExcelService;
    private final PurchaseOrderRepository purchaseOrderRepository; // 🌟 주입 추가

    // URL: GET http://localhost:8080/api/jms-rpa/send-test?to=내메일&orderItemId=발주ID

    @GetMapping("/send-test")
    public String sendTestEmail(@RequestParam("to") String toEmail,
                                @RequestParam("orderItemId") Integer orderItemId) {
        try {
            // 발주 데이터를 DB에서 타겟 조회합니다.
            PurchaseOrderEntity order = purchaseOrderRepository.findById(orderItemId)
                    .orElseThrow(() -> new IllegalArgumentException("발주 내역을 찾을 수 없습니다. id=" + orderItemId));

            String vendorName = order.getVendorIngredient().getVendor().getVendorName();
            String ingredientName = order.getVendorIngredient().getIngredient().getIngredientName();
            String orderUnit = order.getVendorIngredient().getIngredient().getUnit();
            int finalQty = order.getFinalQty();

            // 동적 수집된 데이터를 RPA 포맷 DTO 리스트에 결합
            List<OrderItemDto> orderList = new java.util.ArrayList<>();
            orderList.add(OrderItemDto.builder()
                    .ingredientId(order.getVendorIngredient().getIngredient().getIngredientId())
                    .ingredientName(ingredientName)
                    .orderQty(finalQty)
                    .ingredientUnit(order.getVendorIngredient().getIngredient().getUnit())
                    .build());

            //  실제 승인 자재 기반 동적 엑셀 명세서 빌드
            String createdExcelFile = rpaExcelService.createOrderExcelSheet(vendorName, orderList);

            // 수신인은 프론트가 준  테스트 메일 주소(toEmail)로  우회 발송
            rpaMailService.sendOrderEmailWithAttachment(toEmail, orderList, createdExcelFile);

            return "🎉 [RPA 테스트 성공] 실제 발주 자재 [" + ingredientName + " " + finalQty + orderUnit + "] 명세서가 테스트 계정(" + toEmail + ")으로 발송되었습니다!";
        } catch (Exception e) {
            return "❌ RPA 테스트 장애 발생: " + e.getMessage();
        }
    }

    // URL: GET http://localhost:8080/api/jms-rpa/approval
    @GetMapping("/approval")
    public String showApprovalPage() {
        return "approval/approval";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/approve")
    public ResponseEntity<?> approveAndSendOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            String vendorName = (String) requestBody.get("vendorName");
            String toEmail = (String) requestBody.get("toEmail");
            List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("orderItems");

            List<OrderItemDto> orderList = new java.util.ArrayList<>();
            for (Map<String, Object> item : items) {
                orderList.add(OrderItemDto.builder()
                        .ingredientName((String) item.get("ingredientName"))
                        .orderQty((Integer) item.get("orderQty"))
                        .build());
            }

            log.info("🎯 [AI 발주 승인 트리거] 거래처: {} ({}) | 총 {}건 명세서 발행 시작", vendorName, toEmail, orderList.size());

            String createdExcelFile = rpaExcelService.createOrderExcelSheet(vendorName, orderList);
            rpaMailService.sendOrderEmailWithAttachment(toEmail, orderList, createdExcelFile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "AI 발주 승인 완료! 엑셀 명세서 첨부 메일이 정상 발송되었습니다."
            ));
        } catch (Exception e) {
            log.error("❌ AI 발주 승인 처리 중 장애 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "RPA 승인 처리 오류: " + e.getMessage()
            ));
        }
    }
}