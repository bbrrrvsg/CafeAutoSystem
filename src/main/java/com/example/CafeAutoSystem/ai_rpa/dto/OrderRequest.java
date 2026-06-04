package com.example.CafeAutoSystem.ai_rpa.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    /** 주문 메뉴명 */
    private String menuName;

    /** 주문 수량 */
    private int quantity;
}
