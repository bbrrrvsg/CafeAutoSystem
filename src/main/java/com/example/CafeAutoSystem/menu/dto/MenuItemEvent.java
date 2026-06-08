package com.example.CafeAutoSystem.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka로 구매 서버에 전달할 메뉴 1개 정보.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemEvent {

    private Long menuId;

    private String menuName;

    private Integer menuPrice;

    private String menuImage;
}