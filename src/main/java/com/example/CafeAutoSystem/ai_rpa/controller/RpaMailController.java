package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import com.example.CafeAutoSystem.ai_rpa.service.RpaExcelService;
import com.example.CafeAutoSystem.ai_rpa.service.RpaMailService;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.common.repository.PurchaseOrderRepository;
import com.example.CafeAutoSystem.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/jms-rpa")
@RequiredArgsConstructor
public class RpaMailController {

    private final RpaMailService rpaMailService;
    private final RpaExcelService rpaExcelService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderService purchaseOrderService;

    @Value("${cafe.manager.password}")
    private String managerPassword;

    @GetMapping("/send-test")
    public String sendTestEmail(@RequestParam("to") String toEmail,
                                @RequestParam("orderItemId") Integer orderItemId) {
        try {
            PurchaseOrderEntity order = purchaseOrderRepository.findById(orderItemId)
                    .orElseThrow(() -> new IllegalArgumentException("발주 내역을 찾을 수 없습니다. id=" + orderItemId));

            String vendorName = order.getVendorIngredient().getVendor().getVendorName();
            String ingredientName = order.getVendorIngredient().getIngredient().getIngredientName();
            String orderUnit = order.getVendorIngredient().getIngredient().getUnit();
            int finalQty = order.getFinalQty();

            int unitPrice = order.getVendorIngredient().getUnitPrice();
            int totalPrice = finalQty * unitPrice;

            List<OrderItemDto> orderList = new java.util.ArrayList<>();
            orderList.add(OrderItemDto.builder()
                    .ingredientId(order.getVendorIngredient().getIngredient().getIngredientId())
                    .ingredientName(ingredientName)
                    .orderQty(finalQty)
                    .ingredientUnit(orderUnit)
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .build());

            purchaseOrderService.approve(order.getOrderItemId(), managerPassword);
            log.info("🔹 [주문 상태 마감] ID: {} | 품목: {} -> COMPLETED 전환 완료",
                    order.getOrderItemId(), order.getVendorIngredient().getIngredient().getIngredientName());

            String createdExcelFile = rpaExcelService.createOrderExcelSheet(vendorName, orderList);
            rpaMailService.sendOrderEmailWithAttachment(toEmail, orderList, createdExcelFile);

            return "🎉 [RPA 테스트 성공] 실제 발주 자재 [" + ingredientName + " " + finalQty + orderUnit + "] 명세서가 테스트 계정(" + toEmail + ")으로 발송되었습니다!";
        } catch (Exception e) {
            return "❌ RPA 테스트 장애 발생: " + e.getMessage();
        }
    }

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
                int orderQty = (Integer) item.get("orderQty");
                int unitPrice = item.get("unitPrice") != null ? (Integer) item.get("unitPrice") : 0;
                int totalPrice = item.get("totalPrice") != null ? (Integer) item.get("totalPrice") : (orderQty * unitPrice);

                orderList.add(OrderItemDto.builder()
                        .ingredientName((String) item.get("ingredientName"))
                        .orderQty(orderQty)
                        .ingredientUnit((String) item.get("ingredientUnit"))
                        .unitPrice(unitPrice)
                        .totalPrice(totalPrice)
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

    // 이메일 링크 클릭 시 작동하는 일괄 승인 및 거래처별 분할 전송 API
    @GetMapping("/approve-from-mail")
    public String approveAndSendOrderFromEmailLink() {
        log.info("🎯 [이메일 승인 트리거] 관리자 메일 링크 클릭 일괄 발주 RPA 및 승인 가동.");

        try {
            // 1. 오직 'PENDING' 상태인 최신 AI 대기 내역만 긁어옵니다.
            List<PurchaseOrderEntity> pendingOrders = purchaseOrderRepository.findByStatus("PENDING");

            if (pendingOrders.isEmpty()) {
                return "<html><body style='text-align:center; padding-top:100px; font-family:sans-serif;'>"
                        + "<h2>⚠️ 알림</h2><p style='color:#64748b;'>이미 처리되었거나 승인할 대기 발주서가 없습니다.</p>"
                        + "</body></html>";
            }

            // 2. 거래처별 그룹핑
            Map<String, List<PurchaseOrderEntity>> groupByVendor = pendingOrders.stream()
                    .filter(o -> o.getVendorIngredient() != null && o.getVendorIngredient().getVendor() != null)
                    .collect(Collectors.groupingBy(
                            o -> o.getVendorIngredient().getVendor().getVendorName()
                    ));

            for (Map.Entry<String, List<PurchaseOrderEntity>> entry : groupByVendor.entrySet()) {
                String vendorName = entry.getKey();
                List<PurchaseOrderEntity> vendorOrders = entry.getValue();
                String vendorEmail = vendorOrders.get(0).getVendorIngredient().getVendor().getManagerEmail();

                List<OrderItemDto> orderList = new ArrayList<>();
                for (PurchaseOrderEntity order : vendorOrders) {

                    int realOrderQty = order.getFinalQty();
                    int unitPrice = order.getVendorIngredient().getUnitPrice();

                    orderList.add(OrderItemDto.builder()
                            .ingredientId(order.getVendorIngredient().getIngredient().getIngredientId())
                            .ingredientName(order.getVendorIngredient().getIngredient().getIngredientName())
                            .orderQty(realOrderQty)
                            .ingredientUnit(order.getVendorIngredient().getIngredient().getUnit())
                            .unitPrice(unitPrice)
                            .totalPrice(realOrderQty * unitPrice)
                            .build());

                    purchaseOrderService.approve(order.getOrderItemId(), managerPassword);
                    log.info("🔹 [주문 상태 마감] ID: {} | 품목: {} -> COMPLETED 전환 완료",
                            order.getOrderItemId(), order.getVendorIngredient().getIngredient().getIngredientName());
                }

                // 엑셀 생성 및 메일 발송
                String createdExcelFile = rpaExcelService.createOrderExcelSheet(vendorName, orderList);
                rpaMailService.sendOrderEmailWithAttachment(vendorEmail, orderList, createdExcelFile);
                log.info("✅ [{}] 거래처 명세서 전송 완료", vendorName);
            }

            // 깔끔하고 고급스러운 인프라 결과 뷰 컴포넌트 출력
            return "<html><body style='text-align:center; padding-top:120px; font-family:sans-serif; background-color:#f8fafc; color:#1e293b;'>"
                    + "<div style='display:inline-block; background:white; padding:40px; border-radius:12px; box-shadow:0 4px 6px -1px rgba(0,0,0,0.1); width:500px;'>"
                    + "<h1 style='color:#10b981; margin-bottom:10px;'>🎉 발주 일괄 승인 완료</h1>"
                    + "<p style='font-size:16px; margin-bottom:25px; color:#64748b;'>대기 중이던 AI 발주서가 최종 승인(COMPLETED) 처리되었으며,<br>실시간 입고 장부 적재 및 거래처 엑셀 전송이 완료되었습니다.</p>"
                    + "<div style='font-size:12px; color:#cbd5e1; border-top:1px solid #f1f5f9; padding-top:15px;'>CafeAutoSystem RPA Engine</div>"
                    + "</div>"
                    + "</body></html>";

        } catch (Exception e) {
            log.error("❌ 승인 에러: {}", e.getMessage());
            return "<html><body style='text-align:center; padding-top:100px; font-family:sans-serif;'>"
                    + "<h2 style='color:#ef4444;'>❌ 발주 승인 실패</h2><p>오류 내용: " + e.getMessage() + "</p>"
                    + "</body></html>";
        }
    }
}