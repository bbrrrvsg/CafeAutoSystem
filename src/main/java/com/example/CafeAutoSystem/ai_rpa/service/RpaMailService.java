package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // 🌟 추가
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RpaMailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderEmailWithAttachment(String toEmail, List<OrderItemDto> orderList, String excelFileName) {
        log.info("📩 [RPA 메일 엔진] {} 거래처로 다중 품목 메일 발송 시작.", toEmail);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("⚡ [CAFE SYSTEM] 자동 발주 요청 및 명세서 송부 건");

            StringBuilder itemDetails = new StringBuilder();
            itemDetails.append("안녕하세요, 카페 자동화 시스템 RPA 엔진입니다.\n\n")
                    .append("본 메일은 시스템에 의해 자동 발송된 발주 요청서입니다.\n")
                    .append("아래 품목에 대한 발주 명세서를 첨부하오니 확인 후 납품 부탁드립니다.\n\n")
                    .append("================ [발주 품목 상세 리스트] ================\n");

            for (OrderItemDto item : orderList) {
                String unitStr = (item.getIngredientUnit() != null) ? item.getIngredientUnit() : "개";
                itemDetails.append("■ 품목명: ").append(item.getIngredientName())
                        .append("  |  수량: ").append(item.getOrderQty()).append(" ").append(unitStr).append("\n");
            }

            itemDetails.append("========================================================\n\n")
                    .append("자세한 산출 이력 및 단가 내역은 첨부된 엑셀 파일을 참조해 주세요.\n")
                    .append("감사합니다.");

            helper.setText(itemDetails.toString());

            File excelFile = new File(excelFileName);
            if (excelFile.exists()) {
                FileSystemResource fileSystemResource = new FileSystemResource(excelFile);
                helper.addAttachment(excelFile.getName(), fileSystemResource);
            }

            javaMailSender.send(mimeMessage);
            log.info("🎉 [RPA 메일 엔진 완료] {} 계정으로 메일 발송 성공!", toEmail);

        } catch (Exception e) {
            log.error("❌ [RPA 메일 엔진 오류] 발송 장애 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}