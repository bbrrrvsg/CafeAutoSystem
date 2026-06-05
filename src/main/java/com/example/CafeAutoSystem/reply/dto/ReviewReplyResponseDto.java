package com.example.CafeAutoSystem.reply.dto;

import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewReplyResponseDto {

    private Long replyId;
    private Long customerReviewId;
    private Long orderId;
    private String replyContent;
    private String replyStatus;
    private String message;
    private String createdAt;
    private String updatedAt;

    public static ReviewReplyResponseDto from(ReviewReply reply, String message) {
        return ReviewReplyResponseDto.builder()
                .replyId(reply.getReplyId())
                .customerReviewId(reply.getCustomerReviewId())
                .orderId(reply.getOrderId())
                .replyContent(reply.getReplyContent())
                .replyStatus(reply.getReplyStatus().name())
                .message(message)
                .createdAt(reply.getCreatedAt() == null ? null : reply.getCreatedAt().toString())
                .updatedAt(reply.getUpdatedAt() == null ? null : reply.getUpdatedAt().toString())
                .build();
    }
}