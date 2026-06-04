package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import com.example.CafeAutoSystem.ai_rpa.service.RpaExcelService;
import com.example.CafeAutoSystem.ai_rpa.service.RpaMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
