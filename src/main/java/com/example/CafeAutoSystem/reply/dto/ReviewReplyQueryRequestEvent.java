package com.example.CafeAutoSystem.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReplyQueryRequestEvent {

    private String requestId;
    private Long customerReviewId;
}