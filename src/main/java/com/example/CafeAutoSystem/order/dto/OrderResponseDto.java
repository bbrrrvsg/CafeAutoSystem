package com.example.CafeAutoSystem.order.dto;

import com.example.CafeAutoSystem.order.entity.CafeOrder;
import com.example.CafeAutoSystem.order.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponseDto {

    private Long orderId;
    private Integer orderPrice;
    private String qrUrl;
    private List<OrderDetailResponseDto> orderDetails;
    private String createdAt;
    private String updatedAt;

    public static OrderResponseDto from(CafeOrder order, List<OrderDetail> details) {
        List<OrderDetailResponseDto> orderDetails = details.stream()
                .map(OrderDetailResponseDto::from)
                .toList();

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderPrice(order.getOrderPrice())
                .qrUrl(order.getQrUrl())
                .orderDetails(orderDetails)
                .createdAt(order.getCreatedAt() == null ? null : order.getCreatedAt().toString())
                .updatedAt(order.getUpdatedAt() == null ? null : order.getUpdatedAt().toString())
                .build();
    }
}