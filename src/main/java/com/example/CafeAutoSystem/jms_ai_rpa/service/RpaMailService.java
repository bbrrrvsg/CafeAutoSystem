package com.example.CafeAutoSystem.jms_ai_rpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RpaMailService {
    private final JavaMailSender javaMailSender;

    /**
     * 거래처로 기본 텍스트 발주서 이메일을 전송하는 메서드
     * @param toEmail   거래처 담당자 이메일 주소
     * @param inputItem 발주 품목 정보 요약 text
     * @param quantity  발주 수량
     */
    public void sendDefaultOrderEmail(String toEmail, String inputItem, int quantity) {
        SimpleMailMessage message = new SimpleMailMessage();
        // 발신자 이메일 주소
        message.setFrom("alstj5220@naver.com");

        // 수신자 설정 (거래처 메일)
        message.setTo(toEmail);

        // 메일 제목 설정
        message.setSubject("[📢 Cafe 발주 요청] 신규 자동 발주서가 도착했습니다.");

        // 메일 본문 내용 작성 (1차 MVP용 기본 텍스트 가공)
        String mailContent = "안녕하세요, Cafe 관리 시스템 자동 RPA 엔진입니다.\n\n"
                + "아래와 같이 품목 발주를 요청하오니 확인 후 배송 부탁드립니다.\n\n"
                + "----------------------------------------\n"
                + "■ 요청 품목: " + inputItem + "\n"
                + "■ 요청 수량: " + quantity + "개 (팩/g)\n"
                + "----------------------------------------\n\n"
                + "본 메일은 시스템에 의해 자동 발송된 메일입니다. 감사합니다.";
        message.setText(mailContent);

        // 메일 진짜로 쏘기!
        javaMailSender.send(message);
    }
}
