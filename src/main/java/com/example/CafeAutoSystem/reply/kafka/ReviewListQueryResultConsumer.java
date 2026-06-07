package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewListQueryResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewListQueryResultConsumer {

    private final ObjectMapper objectMapper;
    private final ReviewListKafkaClient reviewListKafkaClient;

    @KafkaListener(
            topics = "review-list-query-result",
            groupId = "owner-review-list-service"
    )
    public void consume(String message) {
        try {
            log.info("📩 리뷰 목록 조회 결과 수신: {}", message);

            ReviewListQueryResultEvent result =
                    objectMapper.readValue(message, ReviewListQueryResultEvent.class);

            reviewListKafkaClient.complete(result);

        } catch (Exception e) {
            log.error("리뷰 목록 조회 결과 처리 실패", e);
        }
    }
}
