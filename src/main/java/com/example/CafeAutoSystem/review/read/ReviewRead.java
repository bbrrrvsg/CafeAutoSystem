package com.example.CafeAutoSystem.review.read;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 구매 서버에서 작성된 리뷰를 사장 서버가 조회하기 위한 read model.
 *
 * 원본 리뷰는 구매 서버 DB에 있고,
 * 사장 서버는 review.created 이벤트를 받아 이 테이블을 갱신한다.
 */
@Entity
@Table(
        name = "review_read",
        indexes = {
                @Index(name = "idx_review_read_order_id", columnList = "order_id"),
                @Index(name = "idx_review_read_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRead {

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "order_id")
    private Long orderId;

    @Lob
    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent;

    @Column(name = "customer_created_at")
    private String customerCreatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}