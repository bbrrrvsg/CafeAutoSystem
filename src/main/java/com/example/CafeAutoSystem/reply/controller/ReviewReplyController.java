package com.example.CafeAutoSystem.reply.controller;

import com.example.CafeAutoSystem.reply.dto.ReviewListPageResponseDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyResponseDto;
import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import com.example.CafeAutoSystem.reply.kafka.ReviewListKafkaClient;
import com.example.CafeAutoSystem.reply.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/reviews")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;
    private final ReviewListKafkaClient reviewListKafkaClient;

    @GetMapping
    public ResponseEntity<ReviewListPageResponseDto> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewListKafkaClient.requestReviews(page, size));
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