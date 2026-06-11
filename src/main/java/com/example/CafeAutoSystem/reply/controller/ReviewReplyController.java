package com.example.CafeAutoSystem.reply.controller;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사장 답글 컨트롤러.
 *
 * 책임:
 * - 답글 조회
 * - 답글 등록
 * - 답글 수정
 * - 답글 삭제
 *
 * 리뷰 목록 조회는 review.controller.OwnerReviewController가 담당한다.
 */
@RestController
@RequestMapping("/api/owner/reviews")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    @GetMapping("/{customerReviewId}/reply")
    public ResponseEntity<ReviewReplyResponseDto> getReply(
            @PathVariable Long customerReviewId
    ) {
        return ResponseEntity.ok(
                reviewReplyService.getReply(customerReviewId)
        );
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