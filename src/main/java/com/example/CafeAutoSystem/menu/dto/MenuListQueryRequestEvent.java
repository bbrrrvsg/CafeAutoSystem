package com.example.CafeAutoSystem.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구매 서버가 Kafka로 보낸 메뉴 목록 조회 요청 이벤트.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuListQueryRequestEvent {

    private String requestId;
}