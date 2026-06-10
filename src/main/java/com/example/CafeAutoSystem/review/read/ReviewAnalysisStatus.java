package com.example.CafeAutoSystem.review.read;

/**
 * 사장 서버 리뷰 AI 분석 상태.
 */
public enum ReviewAnalysisStatus {

    //분석 대기
    PENDING,
    //분석 중
    PROCESSING,
    //분석완료
    COMPLETED,
    //분석실패
    FAILED;


    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public String toDisplayStatus() {
        return switch (this) {
            case COMPLETED -> "ANALYZED";
            case PROCESSING -> "ANALYZING";
            case FAILED -> "ANALYSIS_FAILED";
            case PENDING -> "REVIEW_RECEIVED";
        };
    }
}