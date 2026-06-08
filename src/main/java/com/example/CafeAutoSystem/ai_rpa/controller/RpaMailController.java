package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import com.example.CafeAutoSystem.ai_rpa.service.RpaExcelService;
import com.example.CafeAutoSystem.ai_rpa.service.RpaMailService;
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

    // 이메일 즉시 전송 테스트 API
    // URL: GET http://localhost:8080/api/jms-rpa/send-test?to=본인수신메일주소
    @GetMapping("/send-test")
    public String sendTestEmail(@RequestParam("to") String toEmail) {
        try {
            String vendor = "매일유통 대리점";

            // 🌟 테스트용 다중 품목 데이터 리스트 주입
            List<OrderItemDto> orderList = new java.util.ArrayList<>();
            orderList.add(OrderItemDto.builder().ingredientName("서울우유 1000ml").orderQty(15).build());
            orderList.add(OrderItemDto.builder().ingredientName("매일 멸균우유").orderQty(10).build());
            orderList.add(OrderItemDto.builder().ingredientName("휘핑크림 500ml").orderQty(5).build());

            // 1. 다중 품목 엑셀 생성
            String createdExcelFile = rpaExcelService.createOrderExcelSheet(vendor, orderList);

            // 2. 다중 품목 엑셀 첨부 메일 발송
            rpaMailService.sendOrderEmailWithAttachment(toEmail, orderList, createdExcelFile);

            return "다중 품목 대량 발주 명세서 생성 및 RPA 이메일 발송 완벽 성공!";
        } catch (Exception e) {
            return "RPA 장애 발생: " + e.getMessage();
        }
    }

    // URL: GET http://localhost:8080/api/jms-rpa/approval
    @GetMapping("/approval")
    public String showApprovalPage() {
        // src/main/webapp/WEB-INF/views/approval/approval.jsp
        return "approval/approval";
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveAndSendOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            // 프론트에서 넘겨준 거래처명과 수신 메일 주소 수거
            String vendorName = (String) requestBody.get("vendorName");       // 예: "매일유통 대리점"
            String toEmail = (String) requestBody.get("toEmail");             // 예: "vendor@naver.com"

            // 프론트에서 넘어온 AI 기반 최종 발주 품목 리스트 수거
            List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("orderItems");

            // DTO 규격으로 리스트 변환
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
