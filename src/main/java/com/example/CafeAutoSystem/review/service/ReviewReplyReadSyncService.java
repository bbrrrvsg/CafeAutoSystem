package com.example.CafeAutoSystem.review.service;

import com.example.CafeAutoSystem.review.dto.ReviewRepliedPayload;
import com.example.CafeAutoSystem.review.entity.ReviewRead;
import com.example.CafeAutoSystem.review.repository.ReviewReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 구매 서버 답글 이벤트를 사장 서버 review_read에 반영하는 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewReplyReadSyncService {

    private final ReviewReadRepository reviewReadRepository;

    @Transactional
    public void applyReplied(ReviewRepliedPayload payload) {
        ReviewRead reviewRead = findReviewRead(payload);

        reviewRead.applyReply(
                payload.getReplyContent(),
                payload.getReplyStatus(),
                payload.getRepliedAt(),
                payload.getReplyUpdatedAt()
        );

        log.info("review_read 답글 등록 반영 완료 reviewId={}, orderId={}",
                payload.getReviewId(),
                payload.getOrderId());
    }

    @Transactional
    public void applyReplyUpdated(ReviewRepliedPayload payload) {
        ReviewRead reviewRead = findReviewRead(payload);

        reviewRead.applyReply(
                payload.getReplyContent(),
                payload.getReplyStatus(),
                payload.getRepliedAt(),
                payload.getReplyUpdatedAt()
        );

        log.info("review_read 답글 수정 반영 완료 reviewId={}, orderId={}",
                payload.getReviewId(),
                payload.getOrderId());
    }

    @Transactional
    public void applyReplyDeleted(ReviewRepliedPayload payload) {
        ReviewRead reviewRead = findReviewRead(payload);

        reviewRead.deleteReply(
                payload.getReplyStatus(),
                payload.getReplyUpdatedAt()
        );

        log.info("review_read 답글 삭제 반영 완료 reviewId={}, orderId={}",
                payload.getReviewId(),
                payload.getOrderId());
    }

    private ReviewRead findReviewRead(ReviewRepliedPayload payload) {
        if (payload == null || payload.getReviewId() == null) {
            throw new RuntimeException("답글 이벤트 payload 또는 reviewId가 비어 있습니다.");
        }

        return reviewReadRepository.findById(payload.getReviewId())
                .orElseThrow(() -> new RuntimeException(
                        "review_read를 찾을 수 없습니다. reviewId=" + payload.getReviewId()
                ));
    }
}