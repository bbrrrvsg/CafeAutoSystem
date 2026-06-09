package com.example.CafeAutoSystem.review.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OwnerReviewPageResponseDto {

    private List<OwnerReviewListItemDto> reviews;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}