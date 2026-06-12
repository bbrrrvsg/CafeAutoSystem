package com.example.CafeAutoSystem.review.kafka;

import com.example.CafeAutoSystem.review.dto.ReviewRepliedPayload;
import com.example.CafeAutoSystem.review.service.ReviewReplyReadSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

/**
 * 구매 서버 답글 이벤트 Consumer.
 *
 * 하나의 Consumer에서 답글 등록/수정/삭제 이벤트를 모두 처리한다.
 *
 * topics:
 * - review.replied
 * - review.reply.updated
 * - review.reply.deleted
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewReplyEventConsumer {

    private static final String TOPIC_REPLIED = "review.replied";
    private static final String TOPIC_REPLY_UPDATED = "review.reply.updated";
    private static final String TOPIC_REPLY_DELETED = "review.reply.deleted";

    private final ObjectMapper objectMapper;
    private final ReviewReplyReadSyncService reviewReplyReadSyncService;

    @KafkaListener(
            topics = {
                    TOPIC_REPLIED,
                    TOPIC_REPLY_UPDATED,
                    TOPIC_REPLY_DELETED
            },
            groupId = "owner-review-reply-event-service"
    )
    public void consume(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String message = record.value();

        try {
            log.info("리뷰 답글 이벤트 수신 topic={}, message={}", topic, message);

            ReviewRepliedPayload payload =
                    objectMapper.readValue(message, ReviewRepliedPayload.class);

            switch (topic) {
                case TOPIC_REPLIED -> reviewReplyReadSyncService.applyReplied(payload);
                case TOPIC_REPLY_UPDATED -> reviewReplyReadSyncService.applyReplyUpdated(payload);
                case TOPIC_REPLY_DELETED -> reviewReplyReadSyncService.applyReplyDeleted(payload);
                default -> log.warn("처리하지 않는 리뷰 답글 이벤트 topic={}", topic);
            }

        } catch (Exception e) {
            log.error("리뷰 답글 이벤트 처리 실패 topic={}, message={}", topic, message, e);
            throw new RuntimeException(e);
        }
    }
}