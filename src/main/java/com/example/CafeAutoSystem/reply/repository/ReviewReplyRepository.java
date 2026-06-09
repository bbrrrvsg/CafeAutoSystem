package com.example.CafeAutoSystem.reply.repository;

import com.example.CafeAutoSystem.reply.entity.ReplyStatus;
import com.example.CafeAutoSystem.reply.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    boolean existsByCustomerReviewIdAndReplyStatus(
            Long customerReviewId,
            ReplyStatus replyStatus
    );

    Optional<ReviewReply> findByCustomerReviewIdAndReplyStatus(
            Long customerReviewId,
            ReplyStatus replyStatus
    );
}