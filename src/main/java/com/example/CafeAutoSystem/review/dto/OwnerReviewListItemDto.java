package com.example.CafeAutoSystem.review.dto;

import com.example.CafeAutoSystem.review.entity.ReviewRead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사장 리뷰 목록 응답 DTO.
 *
 * review_read 기반으로 화면에 필요한 리뷰/분석/답글 상태를 내려준다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerReviewListItemDto {

    private Long reviewId;

    private Long orderId;

    private String reviewContent;

    private String analysisStatus;

    private String analysisResultJson;

    private String analyzedAt;

    private String customerCreatedAt;

    private Boolean hasReply;

    private String replyContent;

    private String replyStatus;

    private String repliedAt;

    private String replyUpdatedAt;

    public static OwnerReviewListItemDto from(ReviewRead reviewRead) {
        boolean hasReply =
                "ACTIVE".equals(reviewRead.getReplyStatus())
                        && reviewRead.getReplyContent() != null
                        && !reviewRead.getReplyContent().isBlank();

        return OwnerReviewListItemDto.builder()
                .reviewId(reviewRead.getReviewId())
                .orderId(reviewRead.getOrderId())
                .reviewContent(reviewRead.getReviewContent())
                .analysisStatus(
                        reviewRead.getAnalysisStatus() != null
                                ? reviewRead.getAnalysisStatus().name()
                                : null
                )
                .analysisResultJson(reviewRead.getAnalysisResultJson())
                .analyzedAt(
                        reviewRead.getAnalyzedAt() != null
                                ? reviewRead.getAnalyzedAt().toString()
                                : null
                )
                .customerCreatedAt(reviewRead.getCustomerCreatedAt())
                .hasReply(hasReply)
                .replyContent(hasReply ? reviewRead.getReplyContent() : null)
                .replyStatus(reviewRead.getReplyStatus())
                .repliedAt(reviewRead.getRepliedAt())
                .replyUpdatedAt(reviewRead.getReplyUpdatedAt())
                .build();
    }
}