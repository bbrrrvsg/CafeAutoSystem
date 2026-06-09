package com.example.CafeAutoSystem.review.analysis;

/**
 * LLM 분석 대상으로 넘길 최소 데이터.
 *
 * JPA Entity를 외부 API 호출 구간까지 들고 가지 않기 위해 분리한다.
 */
public record ReviewAnalysisTarget(
        Long reviewId,
        String reviewContent
) {
}