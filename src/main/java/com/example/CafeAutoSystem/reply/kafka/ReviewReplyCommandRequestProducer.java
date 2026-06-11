package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyCommandRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ReviewReplyCommandRequestProducer {

    private static final String TOPIC = "review.reply.command.request";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(ReviewReplyCommandRequestEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.getRequestId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✅ 답글 명령 요청 발행 성공 requestId="
                                    + event.getRequestId()
                                    + ", commandType="
                                    + event.getCommandType());
                        } else {
                            System.out.println("❌ 답글 명령 요청 발행 실패 requestId="
                                    + event.getRequestId());
                            ex.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException("답글 명령 요청 이벤트 변환 실패", e);
        }
    }
}