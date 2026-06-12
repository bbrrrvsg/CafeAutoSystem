package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyCommandResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewReplyCommandResultConsumer {

    private final ObjectMapper objectMapper;
    private final ReviewReplyKafkaClient reviewReplyKafkaClient;

    @KafkaListener(
            topics = "review.reply.command.result",
            groupId = "owner-review-reply-command-result-service"
    )
    public void consume(String message) {
        try {
            log.info("답글 명령 결과 수신: {}", message);

            ReviewReplyCommandResultEvent result =
                    objectMapper.readValue(message, ReviewReplyCommandResultEvent.class);

            reviewReplyKafkaClient.complete(result);

        } catch (Exception e) {
            log.error("답글 명령 결과 처리 실패 message={}", message, e);
        }
    }
}