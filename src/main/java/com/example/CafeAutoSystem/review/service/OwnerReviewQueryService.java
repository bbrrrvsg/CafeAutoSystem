package com.example.CafeAutoSystem.review.service;

import com.example.CafeAutoSystem.review.dto.OwnerReviewListItemDto;
import com.example.CafeAutoSystem.review.dto.OwnerReviewPageResponseDto;
import com.example.CafeAutoSystem.review.entity.ReviewRead;
import com.example.CafeAutoSystem.review.repository.ReviewReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사장 리뷰관리 화면 조회 서비스.
 *
 * 책임:
 * - 사장 서버 review_read 테이블 조회
 * - 구매 서버에 Kafka request/reply로 리뷰 목록을 요청하지 않는다.
 * - review.created / review.analyzed / review.replied 이벤트로 동기화된 read model만 조회한다.
 */
@Service
@RequiredArgsConstructor
public class OwnerReviewQueryService {

    private final ReviewReadRepository reviewReadRepository;

    @Transactional(readOnly = true)
    public OwnerReviewPageResponseDto getReviews(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        Page<ReviewRead> reviewPage =
                reviewReadRepository.findAllByOrderByReviewIdDesc(
                        PageRequest.of(safePage, safeSize)
                );

        List<OwnerReviewListItemDto> reviews = reviewPage.getContent().stream()
                .map(OwnerReviewListItemDto::from)
                .toList();

        return OwnerReviewPageResponseDto.builder()
                .reviews(reviews)
                .page(safePage)
                .size(safeSize)
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }
}