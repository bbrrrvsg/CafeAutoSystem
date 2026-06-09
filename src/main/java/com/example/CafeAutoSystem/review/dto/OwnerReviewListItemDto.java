package com.example.CafeAutoSystem.review.dto;

import com.example.CafeAutoSystem.review.read.ReviewRead;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerReviewListItemDto {

    private Long reviewId;

    private Long orderId;

    private String reviewContent;

    private String createdAt;

    private String status;

    public static OwnerReviewListItemDto from(ReviewRead reviewRead) {
        return OwnerReviewListItemDto.builder()
                .reviewId(reviewRead.getReviewId())
                .orderId(reviewRead.getOrderId())
                .reviewContent(reviewRead.getReviewContent())
                .createdAt(reviewRead.getCustomerCreatedAt())
                .status("REVIEW_RECEIVED")
                .build();
    }
}