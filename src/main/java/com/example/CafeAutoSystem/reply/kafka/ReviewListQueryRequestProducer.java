package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewListQueryRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ReviewListQueryRequestProducer {

    private static final String TOPIC = "review-list-query-request";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(ReviewListQueryRequestEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.getRequestId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✅ 리뷰 목록 조회 요청 발행 성공 requestId=" + event.getRequestId());
                        } else {
                            System.out.println("❌ 리뷰 목록 조회 요청 발행 실패");
                            ex.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException("리뷰 목록 조회 요청 이벤트 변환 실패", e);
        }
    }
}
