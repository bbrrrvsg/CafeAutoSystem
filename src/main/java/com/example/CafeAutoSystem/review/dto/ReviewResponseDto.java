package com.example.CafeAutoSystem.review.dto;

import com.example.CafeAutoSystem.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDto {

    private Long reviewId;
    private Long orderId;
    private String reviewContent;
    private String ownerReply;
    private String message;
    private String createdAt;
    private String updatedAt;

    // 리뷰 작성 성공 응답
    public static ReviewResponseDto createSuccess(Review review, String message) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .orderId(review.getCafeOrder().getOrderId())
                .message(message)
                .createdAt(review.getCreatedAt().toString())
                .build();
    }

    // 리뷰 목록/상세 조회 응답
    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .orderId(review.getCafeOrder().getOrderId())
                .reviewContent(review.getReviewContent())
                .ownerReply(review.getOwnerReply())
                .createdAt(review.getCreatedAt().toString())
                .updatedAt(review.getUpdatedAt().toString())
                .build();
    }

    // 사장님 답글 작성/수정 응답
    public static ReviewResponseDto replySuccess(Review review, String message) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .orderId(review.getCafeOrder().getOrderId())
                .ownerReply(review.getOwnerReply())
                .message(message)
                .createdAt(review.getOwnerReplyCreatedAt() == null ? null : review.getOwnerReplyCreatedAt().toString())
                .build();
    }

    // 사장님 답글 삭제 응답
    public static ReviewResponseDto deleteSuccess(Long reviewId, String message) {
        return ReviewResponseDto.builder()
                .reviewId(reviewId)
                .message(message)
                .build();
    }
}
