package com.example.CafeAutoSystem.reply.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_reply")
public class ReviewReply extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    // 구매 서버 review.review_id
    @Column(name = "customer_review_id", nullable = false)
    private Long customerReviewId;

    // 사장 서버 cafe_order.order_id
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "reply_content", nullable = false, columnDefinition = "TEXT")
    private String replyContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "reply_status", nullable = false)
    private ReplyStatus replyStatus;

    public static ReviewReply create(
            Long customerReviewId,
            Long orderId,
            String replyContent
    ) {
        return ReviewReply.builder()
                .customerReviewId(customerReviewId)
                .orderId(orderId)
                .replyContent(replyContent)
                .replyStatus(ReplyStatus.ACTIVE)
                .build();
    }

    public void updateReply(String replyContent) {
        this.replyContent = replyContent;
    }

    public void deleteReply() {
        this.replyStatus = ReplyStatus.DELETED;
    }
}