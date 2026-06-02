package com.example.CafeAutoSystem.review.entity;


import com.example.CafeAutoSystem.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

}
