package com.example.CafeAutoSystem.review.dto;

import lombok.Data;

@Data
public class ReviewAnalyzedPayload {

    private Long reviewId;

    private Long orderId;

    private String analysisStatus;

    private String analysisResultJson;

    private String analyzedAt;
}