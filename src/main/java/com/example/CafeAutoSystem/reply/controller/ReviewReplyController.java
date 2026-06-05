package com.example.CafeAutoSystem.reply.controller;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/reviews")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

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