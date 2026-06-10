package com.example.CafeAutoSystem.order.service;

import com.example.CafeAutoSystem.global.outbox.OutboxService;
import com.example.CafeAutoSystem.order.dto.OrderCreateRequestEvent;
import com.example.CafeAutoSystem.order.dto.OrderCreateResultEvent;
import com.example.CafeAutoSystem.order.dto.OrderCreatedPayload;
import com.example.CafeAutoSystem.order.dto.OrderItemEvent;
import com.example.CafeAutoSystem.order.dto.OrderResponseDto;
import com.example.CafeAutoSystem.order.entity.CafeOrder;
import com.example.CafeAutoSystem.menu.entity.Menu;
import com.example.CafeAutoSystem.order.entity.OrderDetail;
import com.example.CafeAutoSystem.order.repository.CafeOrderRepository;
import com.example.CafeAutoSystem.menu.repository.MenuRepository;
import com.example.CafeAutoSystem.order.repository.OrderDetailRepository;
import com.example.CafeAutoSystem.stock.dto.OrderRequest;
import com.example.CafeAutoSystem.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 주문 서비스.
 *
 * 현재 주문 생성 요청 자체는 기존 Kafka request/reply 흐름을 유지한다.
 * 다만 주문 저장 성공 후 order.created 이벤트를 outbox에 저장해서
 * 구매 서버 reviewable_order_read 동기화에 사용한다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private static final String ORDER_AGGREGATE_TYPE = "ORDER";

    private final MenuRepository menuRepository;
    private final CafeOrderRepository cafeOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StockService stockService;

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    // 구매 서버가 Kafka로 보낸 주문 생성 요청을 받아 사장 서버 DB에 주문/주문상세를 저장한다.
    public OrderCreateResultEvent createOrderFromEvent(OrderCreateRequestEvent event) {
        validateOrderEvent(event);

        int totalPrice = calculateTotalPrice(event);

        CafeOrder savedOrder = cafeOrderRepository.save(
                CafeOrder.create(totalPrice)
        );

        saveOrderDetails(savedOrder, event);

        /*
         * 주문 저장 + 주문상세 저장 + 재고 처리 + outbox 저장이
         * 하나의 @Transactional 안에서 처리된다.
         *
         * 이후 OutboxRelay가 order.created를 Kafka로 발행하고,
         * 구매 서버는 reviewable_order_read를 갱신한다.
         */
        publishOrderCreatedEvent(savedOrder);

        return OrderCreateResultEvent.builder()
                .requestId(event.getRequestId())
                .orderId(savedOrder.getOrderId())
                .orderPrice(savedOrder.getOrderPrice())
                .createdAt(savedOrder.getCreatedAt() == null ? null : savedOrder.getCreatedAt().toString())
                .success(true)
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

    private void publishOrderCreatedEvent(CafeOrder order) {
        OrderCreatedPayload payload = OrderCreatedPayload.builder()
                .orderId(order.getOrderId())
                .orderPrice(order.getOrderPrice())
                .createdAt(order.getCreatedAt() == null ? null : order.getCreatedAt().toString())
                .build();

        JsonNode payloadNode = objectMapper.valueToTree(payload);

        /*
         * aggregateId / kafkaKey는 orderId로 잡는다.
         * 이유:
         * - 구매 서버 reviewable_order_read의 PK가 order_id
         * - 같은 주문 관련 이벤트 순서를 같은 파티션에서 보장하기 위함
         */
        outboxService.saveEvent(
                "order.created",
                "order.created",
                ORDER_AGGREGATE_TYPE,
                String.valueOf(order.getOrderId()),
                payloadNode
        );
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

            stockService.processOrder(new OrderRequest(menu.getMenuName(), item.getQuantity()));
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