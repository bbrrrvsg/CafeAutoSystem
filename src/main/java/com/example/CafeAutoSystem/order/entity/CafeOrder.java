package com.example.CafeAutoSystem.order.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;


import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cafe_order")
public class CafeOrder extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "qr_url")
    private String qrUrl;

    @Column(name = "order_price")
    private Integer orderPrice;

    public static CafeOrder create(Integer orderPrice) {
        return CafeOrder.builder()
                .orderPrice(orderPrice)
                .build();
    }

    public void updateQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
