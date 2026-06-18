package com.example.CafeAutoSystem.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SearchResponse {
    private String keyword;
    private int total;
    private Map<String, List<SearchResultDto>> results;
}
