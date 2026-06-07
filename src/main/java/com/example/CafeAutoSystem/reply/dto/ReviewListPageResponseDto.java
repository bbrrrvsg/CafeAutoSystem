package com.example.CafeAutoSystem.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListPageResponseDto {

    private List<ReviewListItemDto> reviews;
    private int page;
    private int size;
    private long totalCount;
    private String message;

    public static ReviewListPageResponseDto from(ReviewListQueryResultEvent event) {
        return ReviewListPageResponseDto.builder()
                .reviews(event.getReviews())
                .page(event.getPage())
                .size(event.getSize())
                .totalCount(event.getTotalCount())
                .message(event.getMessage())
                .build();
    }
}
