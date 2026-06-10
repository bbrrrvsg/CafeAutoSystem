package com.example.CafeAutoSystem.review.dto;

import com.example.CafeAutoSystem.review.entity.ReviewAnalysisStatus;
import com.example.CafeAutoSystem.review.entity.ReviewRead;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerReviewListItemDto {

    private Long reviewId;

    private Long orderId;

    private String reviewContent;

    private String analysisStatus;

    private Boolean analysisCompleted;

    private String analysisResultJson;

    private String analyzedAt;

    private String createdAt;

    private String status;



    public static OwnerReviewListItemDto from(ReviewRead reviewRead) {
        ReviewAnalysisStatus analysisStatus = reviewRead.getAnalysisStatus();

        if (analysisStatus == null) {
            analysisStatus = ReviewAnalysisStatus.PENDING;
        }

        return OwnerReviewListItemDto.builder()
                .reviewId(reviewRead.getReviewId())
                .orderId(reviewRead.getOrderId())
                .reviewContent(reviewRead.getReviewContent())
                .analysisStatus(analysisStatus.name())
                .analysisCompleted(analysisStatus.isCompleted())
                .analysisResultJson(reviewRead.getAnalysisResultJson())
                .analyzedAt(reviewRead.getAnalyzedAt() == null ? null : reviewRead.getAnalyzedAt().toString())
                .createdAt(reviewRead.getCustomerCreatedAt())
                .status(analysisStatus.toDisplayStatus())
                .build();
    }
}