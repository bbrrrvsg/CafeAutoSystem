package com.example.CafeAutoSystem.reply.service;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.entity.ReplyStatus;
import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import com.example.CafeAutoSystem.reply.repository.ReviewReplyRepository;
import com.example.CafeAutoSystem.review.repository.CafeOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final CafeOrderRepository cafeOrderRepository;

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

        /*
         * 2차 확장 지점:
         * 답글 저장 성공 후 reply-created Kafka 이벤트 발행
         * → SMS 서버가 받아서 고객에게 문자 전송
         */

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

        return ReviewReplyResponseDto.from(reply, "답글이 수정되었습니다.");
    }

    public ReviewReplyResponseDto deleteReply(Long customerReviewId) {
        validateCustomerReviewId(customerReviewId);

        ReviewReply reply = findActiveReply(customerReviewId);

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