package com.example.CafeAutoSystem.review.controller;

import com.example.CafeAutoSystem.review.dto.ReviewCreateRequestDto;
import com.example.CafeAutoSystem.review.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.review.dto.ReviewResponseDto;
import com.example.CafeAutoSystem.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewCreateRequestDto request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<ReviewResponseDto> createReply(
            @PathVariable Long reviewId,
            @RequestBody ReviewReplyRequestDto request) {
        return ResponseEntity.ok(reviewService.createReply(reviewId, request));
    }

    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<ReviewResponseDto> updateReply(
            @PathVariable Long reviewId,
            @RequestBody ReviewReplyRequestDto request) {
        return ResponseEntity.ok(reviewService.updateReply(reviewId, request));
    }

    @DeleteMapping("/{reviewId}/reply")
    public ResponseEntity<ReviewResponseDto> deleteReply(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteReply(reviewId));
    }
}
