package com.example.CafeAutoSystem.review.kafka;

import com.example.CafeAutoSystem.global.event.EventEnvelope;
import com.example.CafeAutoSystem.global.event.ProcessedEventService;
import com.example.CafeAutoSystem.review.dto.ReviewAnalyzedPayload;
import com.example.CafeAutoSystem.review.entity.ReviewAnalysisStatus;
import com.example.CafeAutoSystem.review.entity.ReviewRead;
import com.example.CafeAutoSystem.review.repository.ReviewReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

/**
 * 구매 서버에서 발행한 review.analyzed 이벤트를 받아
 * 사장 서버 review_read의 분석 상태와 결과를 갱신한다.
 *
 * 주의:
 * - reviewContent는 payload에 포함하지 않는다.
 * - 따라서 review.created 이벤트가 먼저 처리되어 review_read가 존재해야 한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAnalyzedConsumer {

    private final ObjectMapper objectMapper;
    private final ReviewReadRepository reviewReadRepository;
    private final ProcessedEventService processedEventService;

    @Transactional
    @KafkaListener(
            topics = "review.analyzed",
            groupId = "owner-review-analyzed-service"
    )
    public void consume(String message) {
        try {
            EventEnvelope event = objectMapper.readValue(message, EventEnvelope.class);

            boolean firstProcess = processedEventService.tryMarkProcessed(
                    event.getEventId(),
                    event.getEventType()
            );

            if (!firstProcess) {
                log.info("중복 review.analyzed 이벤트 skip eventId={}", event.getEventId());
                return;
            }

            ReviewAnalyzedPayload payload =
                    objectMapper.treeToValue(event.getPayload(), ReviewAnalyzedPayload.class);

            ReviewRead reviewRead = reviewReadRepository.findById(payload.getReviewId())
                    .orElseThrow(() -> new RuntimeException(
                            "review_read를 찾을 수 없습니다. reviewId=" + payload.getReviewId()
                    ));

            ReviewAnalysisStatus status = ReviewAnalysisStatus.valueOf(
                    payload.getAnalysisStatus()
            );

            LocalDateTime analyzedAt = parseDateTime(payload.getAnalyzedAt());

            reviewRead.applyAnalysisResult(
                    status,
                    payload.getAnalysisResultJson(),
                    analyzedAt
            );

            log.info("✅ review.analyzed 반영 완료 reviewId={}, status={}",
                    payload.getReviewId(),
                    payload.getAnalysisStatus()
            );

        } catch (Exception e) {
            log.error("review.analyzed 이벤트 처리 실패 message={}", message, e);
            throw new RuntimeException(e);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value.replace(" ", "T"));
    }
}