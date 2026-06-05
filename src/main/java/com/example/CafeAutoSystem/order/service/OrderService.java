package com.example.CafeAutoSystem.order.service;

import com.example.CafeAutoSystem.order.dto.OrderCreateRequestEvent;
import com.example.CafeAutoSystem.order.dto.OrderCreateResultEvent;
import com.example.CafeAutoSystem.order.dto.OrderItemEvent;
import com.example.CafeAutoSystem.order.dto.OrderResponseDto;
import com.example.CafeAutoSystem.review.entity.CafeOrder;
import com.example.CafeAutoSystem.review.entity.Menu;
import com.example.CafeAutoSystem.review.entity.OrderDetail;
import com.example.CafeAutoSystem.review.repository.CafeOrderRepository;
import com.example.CafeAutoSystem.review.repository.MenuRepository;
import com.example.CafeAutoSystem.review.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final MenuRepository menuRepository;
    private final CafeOrderRepository cafeOrderRepository;
    private final OrderDetailRepository orderDetailRepository;

    // 구매 서버가 Kafka로 보낸 주문 생성 요청을 받아 사장 서버 DB에 주문/주문상세를 저장한다.
    public OrderCreateResultEvent createOrderFromEvent(OrderCreateRequestEvent event) {
        validateOrderEvent(event);

        int totalPrice = calculateTotalPrice(event);

        CafeOrder savedOrder = cafeOrderRepository.save(
                CafeOrder.create(totalPrice)
        );

        saveOrderDetails(savedOrder, event);

        return OrderCreateResultEvent.builder()
                .requestId(event.getRequestId())
                .orderId(savedOrder.getOrderId())
                .orderPrice(savedOrder.getOrderPrice())
                .createdAt(savedOrder.getCreatedAt() == null ? null : savedOrder.getCreatedAt().toString())
                .build();
    }

    // 사장 서버에서 주문 상세 조회용 API에 사용한다.
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        CafeOrder order = cafeOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        List<OrderDetail> details = orderDetailRepository.findByCafeOrder(order);

        return OrderResponseDto.from(order, details);
    }

    private int calculateTotalPrice(OrderCreateRequestEvent event) {
        int totalPrice = 0;

        for (OrderItemEvent item : event.getItems()) {
            Menu menu = findMenu(item.getMenuId());
            totalPrice += menu.getMenuPrice() * item.getQuantity();
        }

        return totalPrice;
    }

    private void saveOrderDetails(CafeOrder savedOrder, OrderCreateRequestEvent event) {
        for (OrderItemEvent item : event.getItems()) {
            Menu menu = findMenu(item.getMenuId());

            OrderDetail orderDetail = OrderDetail.create(
                    savedOrder,
                    menu,
                    item.getQuantity()
            );

            orderDetailRepository.save(orderDetail);

            /*
             * 재고 차감 기능이 있으면 여기에서 호출하면 됩니다.
             *
             * stockService.decreaseByMenuId(menu.getMenuId(), item.getQuantity());
             */
        }
    }

    private Menu findMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuId));
    }

    private void validateOrderEvent(OrderCreateRequestEvent event) {
        if (event.getRequestId() == null || event.getRequestId().isBlank()) {
            throw new RuntimeException("requestId는 필수입니다.");
        }

        if (event.getItems() == null || event.getItems().isEmpty()) {
            throw new RuntimeException("주문 메뉴는 최소 1개 이상이어야 합니다.");
        }

        for (OrderItemEvent item : event.getItems()) {
            if (item.getMenuId() == null) {
                throw new RuntimeException("menuId는 필수입니다.");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("quantity는 1 이상이어야 합니다.");
            }
        }
    }
}