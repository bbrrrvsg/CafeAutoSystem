package com.example.CafeAutoSystem.jms_ai_rpa.controller;

import com.example.CafeAutoSystem.jms_ai_rpa.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AiOrderController {
    /**
     * 🖥️ 사이드바 [AI 발주 관리] 클릭 시 동적 대시보드 화면 반환
     */
    @GetMapping("/ai-order")
    public String aiOrderPage(Model model) {
        log.info("AI 발주 관리 대시보드 독립 컨트롤러 작동 시작");

        try {
            // 1. 임시 가방(DTO)에 리얼 분석 데이터를 담습니다.
            List<OrderItemDto> orderList = new ArrayList<>();
            orderList.add(OrderItemDto.builder()
                    .ingredientName("원두 (블렌드)")
                    .orderQty(5)
                    .predictedRequiredQty(8)
                    .currentStock(1)
                    .unitPrice(32000)
                    .totalPrice(0).build());

            orderList.add(OrderItemDto.builder()
                    .ingredientName("우유 (1L)")
                    .orderQty(20)
                    .predictedRequiredQty(40)
                    .currentStock(3)
                    .unitPrice(2500)
                    .totalPrice(0).build());

            orderList.add(OrderItemDto.builder()
                    .ingredientName("플라스틱 컵")
                    .orderQty(300)
                    .predictedRequiredQty(500)
                    .currentStock(40)
                    .unitPrice(150)
                    .totalPrice(0).build());

            orderList.add(OrderItemDto.builder()
                    .ingredientName("바닐라 시럽")
                    .orderQty(3)
                    .predictedRequiredQty(5)
                    .currentStock(0)
                    .unitPrice(12000)
                    .totalPrice(0).build());

            // 2. 총 발주 예상 금액 자동 연산 및 각 아이템별 예상 금액 계산
            int totalOrderPrice = 0;
            for (OrderItemDto item : orderList) {
                item.setTotalPrice(item.getOrderQty() * item.getUnitPrice());
                totalOrderPrice += item.getTotalPrice();
            }

            // 3. 2차 고도화 조건인 이상치 감지(AI_ERROR) 제어용 플래그
            String aiStatus = "NORMAL";
            // String aiStatus = "AI_ERROR"; // 👈 이상치 배너 테스트용

            // 4. JSP 화면(ai-order.jsp)으로 데이터 전달
            model.addAttribute("orderList", orderList);
            model.addAttribute("totalOrderPrice", String.format("%,d", totalOrderPrice));
            model.addAttribute("aiStatus", aiStatus);

        } catch (Exception e) {
            log.error("❌ AI 대시보드 데이터 바인딩 오류: {}", e.getMessage());
        }

        // ViewResolver가 /WEB-INF/views/ai-order/ai-order.jsp 경로를 찾을 수 있도록 기존 뷰 이름 유지
        return "ai-order/ai-order";
    }
}
