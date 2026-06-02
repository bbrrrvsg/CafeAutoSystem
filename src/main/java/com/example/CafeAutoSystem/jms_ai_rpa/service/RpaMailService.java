package com.example.CafeAutoSystem.jms_ai_rpa.service;

import com.example.CafeAutoSystem.jms_ai_rpa.dto.OrderItemDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
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

    /**
     * ✉️ 엑셀 발주 명세서 파일을 첨부하여 거래처에 자동으로 이메일을 발송하는 RPA 메일 엔진
     * @param toEmail        거래처 담당자 이메일
     * @param ingredientName 품목명
     * @param orderQty       발주 수량
     * @param excelFileName  첨부할 엑셀 파일 이름
     */
    public void sendOrderEmailWithAttachment(String toEmail, List<OrderItemDto> orderList, String excelFileName) {
        log.info("📩 [RPA 메일 엔진] {} 거래처로 다중 품목 메일 발송 시작.", toEmail);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("alstj5220@naver.com");
            helper.setTo(toEmail);
            helper.setSubject("⚡ [CAFE SYSTEM] 자동 발주 요청 및 명세서 송부 건");

            // 🌟 [핵심] StringBuilder와 for문으로 본문 품목 리스트 문자열 조립하기
            StringBuilder itemDetails = new StringBuilder();
            itemDetails.append("안녕하세요, 카페 자동화 시스템 RPA 엔진입니다.\n\n")
                    .append("본 메일은 시스템에 의해 자동 발송된 발주 요청서입니다.\n")
                    .append("아래 품목에 대한 발주 명세서를 첨부하오니 확인 후 납품 부탁드립니다.\n\n")
                    .append("================ [발주 품목 상세 리스트] ================\n");

            // for문 전개!
            for (com.example.CafeAutoSystem.jms_ai_rpa.dto.OrderItemDto item : orderList) {
                itemDetails.append("■ 품목명: ").append(item.getIngredientName())
                        .append("  |  수량: ").append(item.getOrderQty()).append(" 개\n");
            }

            itemDetails.append("========================================================\n\n")
                    .append("자세한 산출 이력 및 단가 내역은 첨부된 엑셀 파일을 참조해 주세요.\n")
                    .append("감사합니다.");

            helper.setText(itemDetails.toString());

            // 📎 엑셀 파일 첨부 영역
            File excelFile = new File(excelFileName);
            if (excelFile.exists()) {
                FileSystemResource fileSystemResource = new FileSystemResource(excelFile);
                helper.addAttachment(excelFile.getName(), fileSystemResource);
            }

            javaMailSender.send(mimeMessage);
            log.info("🎉 [RPA 메일 엔진 완료] {} 계정으로 다중 품목 메일 발송 성공!", toEmail);

        } catch (Exception e) {
            log.error("❌ [RPA 메일 엔진 오류] 발송 장애 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
