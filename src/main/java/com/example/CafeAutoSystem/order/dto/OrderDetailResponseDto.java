package com.example.CafeAutoSystem.order.dto;

import com.example.CafeAutoSystem.review.entity.Menu;
import com.example.CafeAutoSystem.review.entity.OrderDetail;
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

    public static OrderDetailResponseDto from(OrderDetail orderDetail) {
        Menu menu = orderDetail.getMenu();

        return OrderDetailResponseDto.builder()
                .orderDetailId(orderDetail.getOrderDetailId())
                .menuId(menu.getMenuId())
                .menuName(menu.getMenuName())
                .menuPrice(menu.getMenuPrice())
                .quantity(orderDetail.getQuantity())
                .totalPrice(menu.getMenuPrice() * orderDetail.getQuantity())
                .build();
    }
}