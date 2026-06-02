package com.example.CafeAutoSystem.review.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "review_content")
    private String reviewContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private CafeOrder cafeOrder;

    @Column(name = "owner_reply")
    private String ownerReply;

    @Column(name = "owner_reply_created_at")
    private LocalDateTime ownerReplyCreatedAt;

    public static Review create(CafeOrder cafeOrder, String reviewContent) {
        return Review.builder()
                .cafeOrder(cafeOrder)
                .reviewContent(reviewContent)
                .build();
    }

    public void addReply(String content) {
        this.ownerReply = content;
        this.ownerReplyCreatedAt = LocalDateTime.now();
    }

    public void updateReply(String content) {
        this.ownerReply = content;
        this.ownerReplyCreatedAt = LocalDateTime.now();
    }

    public void deleteReply() {
        this.ownerReply = null;
        this.ownerReplyCreatedAt = null;
    }
}
