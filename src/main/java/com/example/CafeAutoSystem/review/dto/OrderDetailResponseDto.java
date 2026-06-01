package com.example.CafeAutoSystem.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDetailResponseDto {

    private Long orderDetailId;
    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer quantity;
    private Integer totalPrice;
}
