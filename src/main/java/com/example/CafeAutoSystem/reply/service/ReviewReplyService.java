package com.example.CafeAutoSystem.reply.service;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyCommandRequestEvent;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyCommandResultEvent;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.kafka.ReviewReplyKafkaClient;
import com.example.CafeAutoSystem.review.entity.ReviewRead;
import com.example.CafeAutoSystem.review.repository.ReviewReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사장 답글 명령 서비스.
 *
 * 책임:
 * - 답글 등록/수정/삭제는 구매 서버에 Kafka command로 요청한다.
 * - 답글 원장은 구매 서버 PostgreSQL review 테이블이다.
 * - 사장 서버는 review_read를 통해 조회만 한다.
 */
@Service
@RequiredArgsConstructor
public class ReviewReplyService {

    private static final String COMMAND_CREATE = "CREATE";
    private static final String COMMAND_UPDATE = "UPDATE";
    private static final String COMMAND_DELETE = "DELETE";

    private final ReviewReplyKafkaClient reviewReplyKafkaClient;
    private final ReviewReadRepository reviewReadRepository;

    public ReviewReplyResponseDto createReply(
            Long customerReviewId,
            Long orderId,
            ReviewReplyRequestDto request
    ) {
        validateReviewId(customerReviewId);
        validateOrderId(orderId);
        validateReplyContent(request);

        ReviewReplyCommandRequestEvent event =
                ReviewReplyCommandRequestEvent.builder()
                        .requestId(reviewReplyKafkaClient.createRequestId())
                        .commandType(COMMAND_CREATE)
                        .reviewId(customerReviewId)
                        .orderId(orderId)
                        .replyContent(request.getReplyContent())
                        .requestedAt(LocalDateTime.now().toString())
                        .build();

        ReviewReplyCommandResultEvent result =
                reviewReplyKafkaClient.requestReplyCommand(event);

        return toResponse(result);
    }

    public ReviewReplyResponseDto updateReply(
            Long customerReviewId,
            ReviewReplyRequestDto request
    ) {
        validateReviewId(customerReviewId);
        validateReplyContent(request);

        ReviewReplyCommandRequestEvent event =
                ReviewReplyCommandRequestEvent.builder()
                        .requestId(reviewReplyKafkaClient.createRequestId())
                        .commandType(COMMAND_UPDATE)
                        .reviewId(customerReviewId)
                        .replyContent(request.getReplyContent())
                        .requestedAt(LocalDateTime.now().toString())
                        .build();

        ReviewReplyCommandResultEvent result =
                reviewReplyKafkaClient.requestReplyCommand(event);

        return toResponse(result);
    }

    public ReviewReplyResponseDto deleteReply(Long customerReviewId) {
        validateReviewId(customerReviewId);

        ReviewReplyCommandRequestEvent event =
                ReviewReplyCommandRequestEvent.builder()
                        .requestId(reviewReplyKafkaClient.createRequestId())
                        .commandType(COMMAND_DELETE)
                        .reviewId(customerReviewId)
                        .requestedAt(LocalDateTime.now().toString())
                        .build();

        ReviewReplyCommandResultEvent result =
                reviewReplyKafkaClient.requestReplyCommand(event);

        return toResponse(result);
    }

    /**
     * 답글 조회는 사장 서버 review_read에서 조회한다.
     *
     * 주의:
     * - 답글 등록 직후 command result는 바로 오지만,
     * - review_read 반영은 review.replied 이벤트를 통해 비동기로 반영된다.
     */
    @Transactional(readOnly = true)
    public ReviewReplyResponseDto getReply(Long customerReviewId) {
        validateReviewId(customerReviewId);

        ReviewRead reviewRead = reviewReadRepository.findById(customerReviewId)
                .orElseThrow(() -> new RuntimeException(
                        "review_read를 찾을 수 없습니다. reviewId=" + customerReviewId
                ));

        boolean hasReply =
                "ACTIVE".equals(reviewRead.getReplyStatus())
                        && reviewRead.getReplyContent() != null
                        && !reviewRead.getReplyContent().isBlank();

        return ReviewReplyResponseDto.builder()
                .customerReviewId(reviewRead.getReviewId())
                .orderId(reviewRead.getOrderId())
                .hasReply(hasReply)
                .replyContent(hasReply ? reviewRead.getReplyContent() : null)
                .replyStatus(reviewRead.getReplyStatus())
                .repliedAt(reviewRead.getRepliedAt())
                .replyUpdatedAt(reviewRead.getReplyUpdatedAt())
                .message(hasReply ? "답글 조회 성공" : "아직 등록된 답글이 없습니다.")
                .build();
    }

    private ReviewReplyResponseDto toResponse(ReviewReplyCommandResultEvent result) {
        boolean hasReply =
                "ACTIVE".equals(result.getReplyStatus())
                        && result.getReplyContent() != null
                        && !result.getReplyContent().isBlank();

        return ReviewReplyResponseDto.builder()
                .customerReviewId(result.getReviewId())
                .orderId(result.getOrderId())
                .hasReply(hasReply)
                .replyContent(hasReply ? result.getReplyContent() : null)
                .replyStatus(result.getReplyStatus())
                .repliedAt(result.getRepliedAt())
                .replyUpdatedAt(result.getReplyUpdatedAt())
                .message(result.getMessage())
                .build();
    }

    private void validateReviewId(Long customerReviewId) {
        if (customerReviewId == null) {
            throw new RuntimeException("리뷰 ID는 필수입니다.");
        }
    }

    private void validateOrderId(Long orderId) {
        if (orderId == null) {
            throw new RuntimeException("주문 ID는 필수입니다.");
        }
    }

    private void validateReplyContent(ReviewReplyRequestDto request) {
        if (request == null || request.getReplyContent() == null || request.getReplyContent().isBlank()) {
            throw new RuntimeException("답글 내용은 필수입니다.");
        }
    }
}