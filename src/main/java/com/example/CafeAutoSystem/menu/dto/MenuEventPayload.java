package com.example.CafeAutoSystem.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * menu.created / menu.updated / menu.deleted 이벤트 payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuEventPayload {

    private Long menuId;

    private String menuName;

    private Integer menuPrice;

    private String menuImage;
    
}