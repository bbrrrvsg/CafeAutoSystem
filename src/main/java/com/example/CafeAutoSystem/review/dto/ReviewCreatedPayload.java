package com.example.CafeAutoSystem.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구매 서버 review.created 이벤트 payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreatedPayload {

    private Long reviewId;

    private Long orderId;

    private String reviewContent;

    private String createdAt;
}