package com.example.CafeAutoSystem.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResultDto {
    private String category;   // 메뉴 / 원자재 / 거래처 / 발주이력 / 재고로그
    private Integer id;
    private String title;      // 핵심 표시 텍스트 (메뉴명, 재료명 등)
    private String subtitle;   // 부가 정보 (단위, 거래처명, 로그타입 등)
    private String status;     // 발주이력 상태 (PENDING / COMPLETED / REJECTED)
    private String createdAt;
}
