package com.example.CafeAutoSystem.review.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import com.example.CafeAutoSystem.menu.dto.MenuDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
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

    @Column(name = "menu_image", length = 255)
    private String menuImage;

    // 엔티티 → DTO
    public MenuDto toDto() {
        return MenuDto.builder()
                .menuId(this.menuId)
                .menuName(this.menuName)
                .menuPrice(this.menuPrice)
                .menuImage(this.menuImage)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
