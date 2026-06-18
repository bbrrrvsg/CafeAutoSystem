package com.example.CafeAutoSystem.search.controller;

import com.example.CafeAutoSystem.search.dto.SearchResponse;
import com.example.CafeAutoSystem.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 통합 검색
     * GET /api/search?keyword=우유
     */
    @GetMapping
    public ResponseEntity<SearchResponse> search(@RequestParam String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(searchService.search(keyword.trim()));
    }
}
