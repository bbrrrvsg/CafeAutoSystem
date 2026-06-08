package com.example.CafeAutoSystem.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * order.created 이벤트 payload.
 *
 * 구매 서버는 이 이벤트를 받아 reviewable_order_read 테이블을 갱신한다.
 * 이후 리뷰 작성 시 구매 서버가 사장 서버에 주문 검증 요청을 하지 않아도 된다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedPayload {

    private Long orderId;

    private Integer orderPrice;

    private String createdAt;
}