package com.example.CafeAutoSystem.review.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 사장 서버 리뷰 분석 스케줄러.
 *
 * review_read에서 PENDING 상태 리뷰를 가져와
 * PROCESSING → COMPLETED 또는 FAILED로 변경한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewAnalysisScheduler {

    private final ReviewAnalysisService reviewAnalysisService;

    @Scheduled(fixedDelay = 5000)
    public void analyzePendingReviews() {
        List<ReviewAnalysisTarget> targets = reviewAnalysisService.claimPendingReviews();

        if (targets.isEmpty()) {
            return;
        }

        log.info("리뷰 분석 대상 {}건 조회", targets.size());

        for (ReviewAnalysisTarget target : targets) {
            try {
                log.info("리뷰 분석 시작 reviewId={}", target.reviewId());

                String analysisResultJson =
                        reviewAnalysisService.analyzeReviewContent(target.reviewContent());

                reviewAnalysisService.completeAnalysis(
                        target.reviewId(),
                        analysisResultJson
                );

            } catch (Exception e) {
                log.error("❌ 리뷰 분석 실패 reviewId={}", target.reviewId(), e);

                reviewAnalysisService.failAnalysis(target.reviewId());
            }
        }
    }
}