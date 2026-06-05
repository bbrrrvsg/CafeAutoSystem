package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyQueryResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ReviewReplyQueryResultProducer {

    private static final String TOPIC = "review-reply-query-result";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(ReviewReplyQueryResultEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.getRequestId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✅ 답글 조회 결과 발행 성공 requestId=" + event.getRequestId());
                        } else {
                            System.out.println("❌ 답글 조회 결과 발행 실패");
                            ex.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException("답글 조회 결과 이벤트 변환 실패", e);
        }
    }
}