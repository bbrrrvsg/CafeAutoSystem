package com.example.CafeAutoSystem.review.read;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 구매 서버에서 작성된 리뷰를 사장 서버가 조회하기 위한 read model.
 *
 * 원본 리뷰와 분석 결과는 구매 서버 DB의 review 테이블에 저장된다.
 * 사장 서버는 review.created / review.analyzed 이벤트를 받아
 * review_read 테이블을 조회용으로 동기화한다.
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

    @Lob
    @Column(name = "analysis_result_json", columnDefinition = "LONGTEXT")
    private String analysisResultJson;

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

    public void applyAnalysisResult(
            ReviewAnalysisStatus analysisStatus,
            String analysisResultJson,
            LocalDateTime analyzedAt
    ) {
        this.analysisStatus = analysisStatus;
        this.analysisResultJson = analysisResultJson;
        this.analyzedAt = analyzedAt;
    }

    @PrePersist
    public void prePersistReviewRead() {
        if (analysisStatus == null) {
            analysisStatus = ReviewAnalysisStatus.PENDING;
        }
    }
}