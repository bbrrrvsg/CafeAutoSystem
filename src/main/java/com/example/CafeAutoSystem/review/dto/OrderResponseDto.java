package com.example.CafeAutoSystem.review.dto;

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
}
