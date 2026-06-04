package com.example.CafeAutoSystem.review.dto;

import com.example.CafeAutoSystem.review.entity.CategoryType;
import com.example.CafeAutoSystem.review.entity.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewAnalysisResponseDto {

    private Long analysisId;

    private Long reviewId;

    private Sentiment sentiment;

    private CategoryType category;

    private String summary;

    private String createdAt;

    private String updatedAt;
}
