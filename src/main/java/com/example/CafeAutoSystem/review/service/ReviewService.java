package com.example.CafeAutoSystem.review.service;

import com.example.CafeAutoSystem.review.dto.ReviewCreateRequestDto;
import com.example.CafeAutoSystem.review.dto.ReviewReplyRequestDto;
import com.example.CafeAutoSystem.review.dto.ReviewResponseDto;
import com.example.CafeAutoSystem.review.entity.CafeOrder;
import com.example.CafeAutoSystem.review.entity.Review;
import com.example.CafeAutoSystem.review.repository.CafeOrderRepository;
import com.example.CafeAutoSystem.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CafeOrderRepository cafeOrderRepository;

    // 고객이 작성한 리뷰를 저장한다.
    public ReviewResponseDto createReview(ReviewCreateRequestDto request) {
        CafeOrder cafeOrder = cafeOrderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + request.getOrderId()));

        if (reviewRepository.existsByCafeOrder(cafeOrder)) {
            throw new RuntimeException("이미 해당 주문에 리뷰가 존재합니다.");
        }

        Review review = Review.create(cafeOrder, request.getReviewContent());
        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.createSuccess(savedReview, "리뷰가 성공적으로 작성되었습니다.");
    }

    // 전체 리뷰 목록을 최신순으로 조회한다.
    public List<ReviewResponseDto> getReviews() {
        return reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(ReviewResponseDto::from)
                .toList();
    }

    // 리뷰 번호로 리뷰 상세 정보를 조회한다.
    public ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewId));

        return ReviewResponseDto.from(review);
    }

    // 사장님이 고객 리뷰에 답글을 작성한다.
    public ReviewResponseDto createReply(Long reviewId, ReviewReplyRequestDto request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewId));

        review.addReply(request.getReplyContent());

        return ReviewResponseDto.replySuccess(review, "답글이 작성되었습니다.");
    }

    // 사장님이 작성한 답글을 수정한다.
    public ReviewResponseDto updateReply(Long reviewId, ReviewReplyRequestDto request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewId));

        review.updateReply(request.getReplyContent());

        return ReviewResponseDto.replySuccess(review, "답글이 수정되었습니다.");
    }

    // 사장님이 작성한 답글을 삭제한다.
    public ReviewResponseDto deleteReply(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewId));

        review.deleteReply();

        return ReviewResponseDto.deleteSuccess(reviewId, "답글이 삭제되었습니다.");
    }
}