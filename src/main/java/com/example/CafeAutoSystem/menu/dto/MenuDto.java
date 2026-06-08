package com.example.CafeAutoSystem.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MenuDto {

    private Long      menuId;
    private String    menuName;   // 메뉴명
    private Integer   menuPrice;  // 판매가
    private String    menuImage;  // 메뉴 사진 URL (/uploads/images/...)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
