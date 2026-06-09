package com.example.CafeAutoSystem.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * reply.created / reply.updated / reply.deleted 이벤트 payload.
 *
 * 구매 서버는 이 이벤트를 받아 reply_read 테이블을 갱신한다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyEventPayload {

    private Long customerReviewId;

    private Long orderId;

    private String replyContent;

    private String repliedAt;
}