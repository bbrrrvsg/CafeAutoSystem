package com.example.CafeAutoSystem.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListItemDto {

    private Long reviewId;
    private Long orderId;
    private String reviewContent;
    private String createdAt;
}
