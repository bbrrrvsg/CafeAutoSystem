package com.example.CafeAutoSystem.stock.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockOutResult {

    private String menuName;
    private int quantity;
    private List<StockDetail> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockDetail {
        private String ingredientName;
        private String unit;
        private int stockUsed;       // 이번 주문으로 차감된 양
        private int currentStock;    // LOG SUM 기준 현재 재고
        private boolean lowStock;    // 안전재고 이하 여부
    }
}
