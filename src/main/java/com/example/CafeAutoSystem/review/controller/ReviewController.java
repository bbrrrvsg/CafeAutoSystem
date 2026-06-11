package com.example.CafeAutoSystem.review.controller;

import com.example.CafeAutoSystem.review.dto.OwnerReviewPageResponseDto;
import com.example.CafeAutoSystem.review.service.OwnerReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사장 리뷰 조회 컨트롤러.
 *
 * 책임:
 * - 리뷰 목록 조회
 * - review_read 조회 모델 기반 조회
 *
 * 답글 등록/수정/삭제는 reply 패키지의 ReviewReplyController가 담당한다.
 */
@RestController
@RequestMapping("/api/owner/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final OwnerReviewQueryService ownerReviewQueryService;

    @GetMapping
    public ResponseEntity<OwnerReviewPageResponseDto> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ownerReviewQueryService.getReviews(page, size)
        );
    }
}