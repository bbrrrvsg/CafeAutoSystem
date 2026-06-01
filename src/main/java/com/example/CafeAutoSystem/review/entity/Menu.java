package com.example.CafeAutoSystem.review.entity;

import com.example.CafeAutoSystem.global.config.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "menu")
public class Menu extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(name = "menu_name" , nullable = false , length = 30)
    private String menuName;

    @Column(name = "menu_price" , nullable = false)
    private Integer menuPrice;



}
