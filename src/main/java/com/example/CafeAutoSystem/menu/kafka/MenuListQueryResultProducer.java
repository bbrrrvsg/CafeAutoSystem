package com.example.CafeAutoSystem.menu.kafka;

import com.example.CafeAutoSystem.menu.dto.MenuListQueryResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

/**
 * 사장 서버 → 구매 서버
 * 메뉴 목록 조회 결과 이벤트 발행.
 */
@Service
@RequiredArgsConstructor
public class MenuListQueryResultProducer {

    private static final String TOPIC = "menu-list-query-result";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(MenuListQueryResultEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.getRequestId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✅ 메뉴 목록 조회 결과 발행 성공 requestId=" + event.getRequestId());
                        } else {
                            System.out.println("❌ 메뉴 목록 조회 결과 발행 실패");
                            ex.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException("메뉴 목록 조회 결과 이벤트 변환 실패", e);
        }
    }
}