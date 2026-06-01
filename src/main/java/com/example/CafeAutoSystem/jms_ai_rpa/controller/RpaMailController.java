package com.example.CafeAutoSystem.jms_ai_rpa.controller;

import com.example.CafeAutoSystem.jms_ai_rpa.service.RpaExcelService;
import com.example.CafeAutoSystem.jms_ai_rpa.service.RpaMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            // 1. [RPA 엑셀 가동] 메일 쏘기 전에 진짜로 바탕화면에 엑셀 서류를 먼저 빌드합니다!
            // 테스트용 데이터 전달 (거래처: 서울원두유통, 품목: 에스프레소 원두, 수량: 18개)
            rpaExcelService.createOrderExcelSheet("서울원두유통", "에스프레소 원두(1kg)", 18);

            // 2. [RPA 메일 발송] 기존 메일 엔진 가동
            rpaMailService.sendDefaultOrderEmail(toEmail, "에스프레소 원두(1kg)", 18);

            return "Excel 생성 및 Mail 발송 성공!";
        } catch (Exception e) {
            return "장애 발생: " + e.getMessage();
        }
    }

    // URL: GET http://localhost:8080/api/jms-rpa/approval
    @GetMapping("/approval")
    public String showApprovalPage() {
        // src/main/webapp/WEB-INF/views/approval/approval.jsp
        return "approval/approval";
    }

}
