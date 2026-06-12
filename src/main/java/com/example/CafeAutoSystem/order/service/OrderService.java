package com.example.CafeAutoSystem.order.service;

import com.example.CafeAutoSystem.global.outbox.OutboxService;
import com.example.CafeAutoSystem.menu.entity.Menu;
import com.example.CafeAutoSystem.menu.repository.MenuRepository;
import com.example.CafeAutoSystem.order.dto.OrderCreateRequestEvent;
import com.example.CafeAutoSystem.order.dto.OrderCreateResultEvent;
import com.example.CafeAutoSystem.order.dto.OrderCreatedPayload;
import com.example.CafeAutoSystem.order.dto.OrderItemEvent;
import com.example.CafeAutoSystem.order.dto.OrderResponseDto;
import com.example.CafeAutoSystem.order.entity.CafeOrder;
import com.example.CafeAutoSystem.order.entity.OrderDetail;
import com.example.CafeAutoSystem.order.repository.CafeOrderRepository;
import com.example.CafeAutoSystem.order.repository.OrderDetailRepository;
import com.example.CafeAutoSystem.stock.dto.OrderRequest;
import com.example.CafeAutoSystem.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
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

    /**
     * 구매 서버가 Kafka로 보낸 주문 생성 요청을 받아
     * 사장 서버 DB에 주문/주문상세를 저장하고,
     * 메뉴 레시피 기준 재고를 차감한다.
     *
     * 정책:
     * - 메뉴가 없으면 실패
     * - 레시피가 없으면 실패
     * - 재고가 부족하면 실패
     * - 성공하면 주문/상세 저장 + 재고 차감 + order.created outbox 저장
     */
    public OrderCreateResultEvent createOrderFromEvent(OrderCreateRequestEvent event) {
        validateOrderEvent(event);

        int totalPrice = calculateTotalPrice(event);

        CafeOrder savedOrder = cafeOrderRepository.save(
                CafeOrder.create(totalPrice)
        );

        saveOrderDetailsAndDecreaseStock(savedOrder, event);

        publishOrderCreatedEvent(savedOrder);

        log.info("주문 생성 성공 requestId={}, orderId={}, orderPrice={}",
                event.getRequestId(),
                savedOrder.getOrderId(),
                savedOrder.getOrderPrice()
        );

        return OrderCreateResultEvent.builder()
                .requestId(event.getRequestId())
                .orderId(savedOrder.getOrderId())
                .orderPrice(savedOrder.getOrderPrice())
                .createdAt(savedOrder.getCreatedAt() == null ? null : savedOrder.getCreatedAt().toString())
                .success(true)
                .message("주문 생성 성공")
                .build();
    }

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

    private void saveOrderDetailsAndDecreaseStock(CafeOrder savedOrder, OrderCreateRequestEvent event) {
        for (OrderItemEvent item : event.getItems()) {
            Menu menu = findMenu(item.getMenuId());

            OrderDetail orderDetail = OrderDetail.create(
                    savedOrder,
                    menu,
                    item.getQuantity()
            );

            orderDetailRepository.save(orderDetail);

            /*
             * 중요:
             * 주문 성공 조건에 재고 차감을 포함한다.
             * StockService 내부에서 MENU_RECIPE가 없거나 재고가 부족하면 예외가 발생한다.
             * 이 예외가 발생하면 @Transactional에 의해 주문 저장도 같이 롤백된다.
             */
            stockService.processOrder(
                    new OrderRequest(menu.getMenuName(), item.getQuantity())
            );
        }
    }

    private void publishOrderCreatedEvent(CafeOrder order) {
        OrderCreatedPayload payload = OrderCreatedPayload.builder()
                .orderId(order.getOrderId())
                .orderPrice(order.getOrderPrice())
                .createdAt(order.getCreatedAt() == null ? null : order.getCreatedAt().toString())
                .build();

        JsonNode payloadNode = objectMapper.valueToTree(payload);

        outboxService.saveEvent(
                "order.created",
                "order.created",
                ORDER_AGGREGATE_TYPE,
                String.valueOf(order.getOrderId()),
                payloadNode
        );
    }

    private Menu findMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuId));
    }

    private void validateOrderEvent(OrderCreateRequestEvent event) {
        if (event == null) {
            throw new RuntimeException("주문 요청 이벤트가 비어 있습니다.");
        }

        if (event.getRequestId() == null || event.getRequestId().isBlank()) {
            throw new RuntimeException("requestId는 필수입니다.");
        }

        if (event.getItems() == null || event.getItems().isEmpty()) {
            throw new RuntimeException("주문 메뉴는 최소 1개 이상이어야 합니다.");
        }

        for (OrderItemEvent item : event.getItems()) {
            if (item == null) {
                throw new RuntimeException("주문 항목이 비어 있습니다.");
            }

            if (item.getMenuId() == null) {
                throw new RuntimeException("menuId는 필수입니다.");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("quantity는 1 이상이어야 합니다.");
            }
        }
    }
}