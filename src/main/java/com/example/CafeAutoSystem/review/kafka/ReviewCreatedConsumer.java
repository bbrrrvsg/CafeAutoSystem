package com.example.CafeAutoSystem.review.kafka;

import com.example.CafeAutoSystem.global.event.EventEnvelope;
import com.example.CafeAutoSystem.global.event.ProcessedEventService;
import com.example.CafeAutoSystem.review.dto.ReviewCreatedPayload;
import com.example.CafeAutoSystem.review.read.ReviewAnalysisStatus;
import com.example.CafeAutoSystem.review.read.ReviewRead;
import com.example.CafeAutoSystem.review.read.ReviewReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * 구매 서버 review.created 이벤트를 받아
 * 사장 서버 review_read 테이블에 저장한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final ProcessedEventService processedEventService;
    private final ReviewReadRepository reviewReadRepository;

    @Transactional
    @KafkaListener(
            topics = "review.created",
            groupId = "owner-review-read-service"
    )
    public void consume(String message) {
        try {
            EventEnvelope event = objectMapper.readValue(message, EventEnvelope.class);

            boolean firstProcess = processedEventService.tryMarkProcessed(
                    event.getEventId(),
                    event.getEventType()
            );

            if (!firstProcess) {
                log.info("중복 review.created 이벤트 skip eventId={}", event.getEventId());
                return;
            }

            ReviewCreatedPayload payload =
                    objectMapper.treeToValue(event.getPayload(), ReviewCreatedPayload.class);

            ReviewRead reviewRead = ReviewRead.builder()
                    .reviewId(payload.getReviewId())
                    .orderId(payload.getOrderId())
                    .reviewContent(payload.getReviewContent())
                    .analysisStatus(ReviewAnalysisStatus.PENDING)
                    .customerCreatedAt(payload.getCreatedAt())
                    .build();

            reviewReadRepository.save(reviewRead);

            log.info("✅ review_read upsert 완료 reviewId={}, orderId={}",
                    payload.getReviewId(), payload.getOrderId());

        } catch (Exception e) {
            log.error("review.created 이벤트 처리 실패 message={}", message, e);
            throw new RuntimeException(e);
        }
    }
}