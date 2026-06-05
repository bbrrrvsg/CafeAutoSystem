package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyQueryRequestEvent;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyQueryResultEvent;
import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import com.example.CafeAutoSystem.reply.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewReplyQueryRequestConsumer {

    private final ObjectMapper objectMapper;
    private final ReviewReplyService reviewReplyService;
    private final ReviewReplyQueryResultProducer resultProducer;

    @KafkaListener(
            topics = "review-reply-query-request",
            groupId = "owner-reply-query-service"
    )
    public void consume(String message) {
        try {
            log.info("📩 답글 조회 요청 수신: {}", message);

            ReviewReplyQueryRequestEvent request =
                    objectMapper.readValue(message, ReviewReplyQueryRequestEvent.class);

            ReviewReply reply =
                    reviewReplyService.findActiveReplyOrNull(
                            request.getCustomerReviewId()
                    );

            ReviewReplyQueryResultEvent result;

            if (reply == null) {
                result = ReviewReplyQueryResultEvent.builder()
                        .requestId(request.getRequestId())
                        .customerReviewId(request.getCustomerReviewId())
                        .hasReply(false)
                        .message("아직 등록된 답글이 없습니다.")
                        .build();
            } else {
                result = ReviewReplyQueryResultEvent.builder()
                        .requestId(request.getRequestId())
                        .customerReviewId(reply.getCustomerReviewId())
                        .orderId(reply.getOrderId())
                        .hasReply(true)
                        .replyContent(reply.getReplyContent())
                        .repliedAt(reply.getUpdatedAt() == null ? null : reply.getUpdatedAt().toString())
                        .message("답글 조회 성공")
                        .build();
            }

            resultProducer.send(result);

        } catch (Exception e) {
            log.error("답글 조회 요청 처리 실패", e);
        }
    }
}