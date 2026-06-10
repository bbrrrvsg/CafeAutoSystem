package com.example.CafeAutoSystem.reply.service;

import com.example.CafeAutoSystem.global.outbox.OutboxService;
import com.example.CafeAutoSystem.reply.dto.ReplyEventPayload;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.entity.ReplyStatus;
import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import com.example.CafeAutoSystem.reply.repository.ReviewReplyRepository;
import com.example.CafeAutoSystem.order.repository.CafeOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

/**
 * 사장 답글 서비스.
 *
 * 변경점:
 * - 답글 작성/수정/삭제 시 Kafka를 직접 발행하지 않는다.
 * - 같은 트랜잭션 안에서 outbox에 reply.* 이벤트를 저장한다.
 * - OutboxRelay가 3초마다 Kafka로 발행한다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewReplyService {

    private static final String REPLY_AGGREGATE_TYPE = "REPLY";

    private final ReviewReplyRepository reviewReplyRepository;
    private final CafeOrderRepository cafeOrderRepository;

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public ReviewReplyResponseDto createReply(
            Long customerReviewId,
            Long orderId,
            ReviewReplyRequestDto request
    ) {
        validateCreateRequest(customerReviewId, orderId, request);

        if (!cafeOrderRepository.existsById(orderId)) {
            throw new RuntimeException("주문을 찾을 수 없습니다: " + orderId);
        }

        if (reviewReplyRepository.existsByCustomerReviewIdAndReplyStatus(
                customerReviewId,
                ReplyStatus.ACTIVE
        )) {
            throw new RuntimeException("이미 해당 리뷰에 답글이 존재합니다.");
        }

        ReviewReply reply = ReviewReply.create(
                customerReviewId,
                orderId,
                request.getReplyContent()
        );

        ReviewReply savedReply = reviewReplyRepository.save(reply);

        // 같은 트랜잭션 안에서 outbox 저장
        publishReplyEvent("reply.created", savedReply);

        return ReviewReplyResponseDto.from(savedReply, "답글이 작성되었습니다.");
    }

    public ReviewReplyResponseDto updateReply(
            Long customerReviewId,
            ReviewReplyRequestDto request
    ) {
        validateCustomerReviewId(customerReviewId);
        validateReplyContent(request);

        ReviewReply reply = findActiveReply(customerReviewId);

        reply.updateReply(request.getReplyContent());

        // 같은 트랜잭션 안에서 outbox 저장
        publishReplyEvent("reply.updated", reply);

        return ReviewReplyResponseDto.from(reply, "답글이 수정되었습니다.");
    }

    public ReviewReplyResponseDto deleteReply(Long customerReviewId) {
        validateCustomerReviewId(customerReviewId);

        ReviewReply reply = findActiveReply(customerReviewId);

        // deleteReply() 호출 전에 payload에 필요한 값을 먼저 이벤트로 저장한다.
        publishReplyEvent("reply.deleted", reply);

        reply.deleteReply();

        return ReviewReplyResponseDto.from(reply, "답글이 삭제되었습니다.");
    }

    @Transactional(readOnly = true)
    public ReviewReply findActiveReplyOrNull(Long customerReviewId) {
        if (customerReviewId == null) {
            return null;
        }

        return reviewReplyRepository
                .findByCustomerReviewIdAndReplyStatus(
                        customerReviewId,
                        ReplyStatus.ACTIVE
                )
                .orElse(null);
    }

    private ReviewReply findActiveReply(Long customerReviewId) {
        return reviewReplyRepository
                .findByCustomerReviewIdAndReplyStatus(
                        customerReviewId,
                        ReplyStatus.ACTIVE
                )
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));
    }

    private void publishReplyEvent(String eventType, ReviewReply reply) {
        ReplyEventPayload payload = ReplyEventPayload.builder()
                .customerReviewId(reply.getCustomerReviewId())
                .orderId(reply.getOrderId())
                .replyContent(reply.getReplyContent())
                .repliedAt(LocalDateTime.now().toString())
                .build();

        JsonNode payloadNode = objectMapper.valueToTree(payload);

        /*
         * aggregateId / kafkaKey는 customerReviewId로 잡는다.
         * 이유:
         * - 구매 서버 reply_read의 PK가 customer_review_id
         * - 같은 리뷰의 답글 created/updated/deleted 순서를 같은 파티션에서 보장하기 위함
         */
        outboxService.saveEvent(
                eventType,
                eventType,
                REPLY_AGGREGATE_TYPE,
                String.valueOf(reply.getCustomerReviewId()),
                payloadNode
        );
    }

    private void validateCreateRequest(
            Long customerReviewId,
            Long orderId,
            ReviewReplyRequestDto request
    ) {
        validateCustomerReviewId(customerReviewId);

        if (orderId == null) {
            throw new RuntimeException("주문 ID는 필수입니다.");
        }

        validateReplyContent(request);
    }

    private void validateCustomerReviewId(Long customerReviewId) {
        if (customerReviewId == null) {
            throw new RuntimeException("고객 리뷰 ID는 필수입니다.");
        }
    }

    private void validateReplyContent(ReviewReplyRequestDto request) {
        if (request == null || request.getReplyContent() == null || request.getReplyContent().isBlank()) {
            throw new RuntimeException("답글 내용은 필수입니다.");
        }
    }
}