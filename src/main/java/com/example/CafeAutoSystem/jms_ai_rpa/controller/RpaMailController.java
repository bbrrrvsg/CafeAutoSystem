package com.example.CafeAutoSystem.jms_ai_rpa.controller;

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

    // 이메일 즉시 전송 테스트 API
    // URL: GET http://localhost:8080/api/jms-rpa/send-test?to=본인수신메일주소
    @GetMapping("/send-test")
    public ResponseEntity<String> sendTestEmail(@RequestParam("to") String toEmail) {
        try {
            // 4번 우유 자재 15팩을 가정하고 테스트 발송
            rpaMailService.sendDefaultOrderEmail(toEmail, "서울우유 1000ml", 15);
            return ResponseEntity.ok("이메일이 성공적으로 발송되었습니다! (Recipient: " + toEmail + ")");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("이메일 발송 중 장애 발생: " + e.getMessage());
        }
    }

    // URL: GET http://localhost:8080/api/jms-rpa/approval
    @GetMapping("/approval")
    public String showApprovalPage() {
        // src/main/webapp/WEB-INF/views/approval/approval.jsp
        return "approval/approval";
    }
}
