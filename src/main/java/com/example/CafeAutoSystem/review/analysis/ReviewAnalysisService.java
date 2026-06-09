package com.example.CafeAutoSystem.review.analysis;

import com.example.CafeAutoSystem.review.read.ReviewAnalysisStatus;
import com.example.CafeAutoSystem.review.read.ReviewRead;
import com.example.CafeAutoSystem.review.read.ReviewReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사장 서버 리뷰 분석 상태 변경과 저장을 담당한다.
 *
 * 중요한 점:
 * - LLM 호출 중에는 DB 트랜잭션을 오래 잡지 않는다.
 * - PENDING → PROCESSING 변경은 짧은 트랜잭션으로 끝낸다.
 * - LLM 호출 후 COMPLETED / FAILED 저장도 별도 트랜잭션으로 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private final ReviewReadRepository reviewReadRepository;
    private final ReviewAnalysisClient reviewAnalysisClient;

    /**
     * 분석 대기 리뷰를 최대 10개 가져오고 PROCESSING으로 선점한다.
     *
     * 이 메서드가 끝나면 트랜잭션이 커밋된다.
     * 그래서 이후 LLM 호출은 DB 트랜잭션 밖에서 실행된다.
     *
     * 왜 이렇게 하냐?
     * - 외부 LLM API 호출은 느리거나 실패할 수 있다.
     * - 그동안 DB 트랜잭션을 잡고 있으면 커넥션 낭비와 락 문제가 생길 수 있다.
     */
    @Transactional
    public List<ReviewAnalysisTarget> claimPendingReviews() {
        List<ReviewRead> pendingReviews =
                reviewReadRepository.findTop10ByAnalysisStatusOrderByReviewIdAsc(
                        ReviewAnalysisStatus.PENDING
                );

        if (pendingReviews.isEmpty()) {
            return List.of();
        }

        for (ReviewRead reviewRead : pendingReviews) {
            reviewRead.markProcessing();
        }

        return pendingReviews.stream()
                .map(reviewRead -> new ReviewAnalysisTarget(
                        reviewRead.getReviewId(),
                        reviewRead.getReviewContent()
                ))
                .toList();
    }

    /**
     * 외부 LLM API 호출.
     *
     * 여기는 일부러 @Transactional을 걸지 않는다.
     * 외부 API 호출 중 DB 트랜잭션을 오래 잡지 않기 위해서다.
     */
    public String analyzeReviewContent(String reviewContent) {
        return reviewAnalysisClient.analyze(reviewContent);
    }

    /**
     * LLM 분석 성공 결과 저장.
     *
     * REQUIRES_NEW를 쓰는 이유:
     * - 분석 하나가 성공하면 그 결과는 독립적으로 바로 커밋한다.
     * - 다른 리뷰 분석이 실패해도 이미 성공한 리뷰 결과가 rollback되지 않게 한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeAnalysis(Long reviewId, String analysisResultJson) {
        ReviewRead reviewRead = reviewReadRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException(
                        "review_read를 찾을 수 없습니다. reviewId=" + reviewId
                ));

        reviewRead.markCompleted(analysisResultJson);

        log.info("✅ 리뷰 분석 완료 저장 reviewId={}, status={}",
                reviewId,
                ReviewAnalysisStatus.COMPLETED
        );
    }

    /**
     * LLM 분석 실패 상태 저장.
     *
     * 지금 MVP에서는 retry_count / error_message를 DB에 저장하지 않는다.
     * 실패하면 FAILED 상태만 저장하고, 상세 원인은 서버 로그로 확인한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failAnalysis(Long reviewId) {
        ReviewRead reviewRead = reviewReadRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException(
                        "review_read를 찾을 수 없습니다. reviewId=" + reviewId
                ));

        reviewRead.markFailed();

        log.warn("⚠️ 리뷰 분석 실패 상태 저장 reviewId={}, status={}",
                reviewId,
                ReviewAnalysisStatus.FAILED
        );
    }
}