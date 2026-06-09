package com.example.CafeAutoSystem.review.read;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 구매 서버에서 작성된 리뷰를 사장 서버가 조회하기 위한 read model.
 *
 * 원본 리뷰는 구매 서버 DB에 있고,
 * 사장 서버는 review.created 이벤트를 받아 이 테이블을 갱신한다.
 * 리뷰 분석은 사장 서버에서 비동기로 수행한다.
 */
@Entity
@Table(
        name = "review_read",
        indexes = {
                @Index(name = "idx_review_read_order_id", columnList = "order_id"),
                @Index(name = "idx_review_read_created_at", columnList = "created_at"),
                @Index(name = "idx_review_read_analysis_status", columnList = "analysis_status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRead extends BaseTime {

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "order_id")
    private Long orderId;

    @Lob
    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent;

    /**
     * 사장 서버에서 LLM 분석 후 저장하는 분석 결과 JSON.
     */
    @Lob
    @Column(name = "analysis_result_json", columnDefinition = "LONGTEXT")
    private String analysisResultJson;

    /**
     * PENDING / PROCESSING / COMPLETED / FAILED
     *
     * EnumType.STRING을 반드시 사용해야 한다.
     * ORDINAL을 쓰면 enum 순서가 바뀔 때 DB 값 의미가 깨진다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false, length = 20)
    private ReviewAnalysisStatus analysisStatus;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    /**
     * 구매 서버에서 고객이 리뷰를 작성한 원본 시간.
     */
    @Column(name = "customer_created_at")
    private String customerCreatedAt;

    public void markPending() {
        this.analysisStatus = ReviewAnalysisStatus.PENDING;
        this.analysisResultJson = null;
        this.analyzedAt = null;
    }

    public void markProcessing() {
        this.analysisStatus = ReviewAnalysisStatus.PROCESSING;
    }

    public void markCompleted(String analysisResultJson) {
        this.analysisStatus = ReviewAnalysisStatus.COMPLETED;
        this.analysisResultJson = analysisResultJson;
        this.analyzedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.analysisStatus = ReviewAnalysisStatus.FAILED;
    }

    @PrePersist
    public void prePersistReviewRead() {
        if (analysisStatus == null) {
            analysisStatus = ReviewAnalysisStatus.PENDING;
        }
    }
}