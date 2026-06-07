package com.example.CafeAutoSystem.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListQueryResultEvent {

    private String requestId;
    private List<ReviewListItemDto> reviews;
    private int page;
    private int size;
    private long totalCount;
    private String message;
}
