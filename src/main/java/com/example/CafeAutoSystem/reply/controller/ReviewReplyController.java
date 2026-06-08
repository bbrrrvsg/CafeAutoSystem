package com.example.CafeAutoSystem.reply.controller;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import com.example.CafeAutoSystem.reply.service.ReviewReplyService;
import com.example.CafeAutoSystem.review.dto.OwnerReviewPageResponseDto;
import com.example.CafeAutoSystem.review.service.OwnerReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 사장 리뷰관리 컨트롤러.
 *
 * 리뷰 목록 조회:
 * - 기존 Kafka request/reply 제거
 * - 구매 서버 review.created 이벤트로 동기화된 review_read 테이블 조회
 */
@RestController
@RequestMapping("/api/owner/reviews")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;
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

    @GetMapping("/{customerReviewId}/reply")
    public ResponseEntity<Map<String, Object>> getReply(@PathVariable Long customerReviewId) {
        ReviewReply reply = reviewReplyService.findActiveReplyOrNull(customerReviewId);

        Map<String, Object> result = new HashMap<>();

        if (reply == null) {
            result.put("hasReply", false);
            result.put("replyContent", null);
        } else {
            result.put("hasReply", true);
            result.put("replyContent", reply.getReplyContent());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{customerReviewId}/reply")
    public ResponseEntity<ReviewReplyResponseDto> createReply(
            @PathVariable Long customerReviewId,
            @RequestParam Long orderId,
            @RequestBody ReviewReplyRequestDto request
    ) {
        return ResponseEntity.ok(
                reviewReplyService.createReply(
                        customerReviewId,
                        orderId,
                        request
                )
        );
    }

    @PutMapping("/{customerReviewId}/reply")
    public ResponseEntity<ReviewReplyResponseDto> updateReply(
            @PathVariable Long customerReviewId,
            @RequestBody ReviewReplyRequestDto request
    ) {
        return ResponseEntity.ok(
                reviewReplyService.updateReply(
                        customerReviewId,
                        request
                )
        );
    }

    @DeleteMapping("/{customerReviewId}/reply")
    public ResponseEntity<ReviewReplyResponseDto> deleteReply(
            @PathVariable Long customerReviewId
    ) {
        return ResponseEntity.ok(
                reviewReplyService.deleteReply(customerReviewId)
        );
    }
}