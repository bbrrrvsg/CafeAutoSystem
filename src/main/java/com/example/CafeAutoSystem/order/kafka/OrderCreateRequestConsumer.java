package com.example.CafeAutoSystem.order.kafka;

import com.example.CafeAutoSystem.order.dto.OrderCreateRequestEvent;
import com.example.CafeAutoSystem.order.dto.OrderCreateResultEvent;
import com.example.CafeAutoSystem.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreateRequestConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final OrderCreateResultProducer orderCreateResultProducer;

    @KafkaListener(
            topics = "order-create-request",
            groupId = "owner-order-service"
    )
    public void consume(String message) {
        OrderCreateRequestEvent event = null;
        try {
            log.info("📩 주문 생성 요청 수신: {}", message);

            event = objectMapper.readValue(message, OrderCreateRequestEvent.class);

            OrderCreateResultEvent result = orderService.createOrderFromEvent(event);
            orderCreateResultProducer.send(result);

        } catch (Exception e) {
            log.error("주문 생성 처리 실패 - requestId={}, 사유={}",
                    event != null ? event.getRequestId() : "unknown", e.getMessage());

            if (event != null && event.getRequestId() != null) {
                OrderCreateResultEvent failResult = OrderCreateResultEvent.builder()
                        .requestId(event.getRequestId())
                        .success(false)
                        .message("주문 처리에 실패했습니다.")
                        .build();
                orderCreateResultProducer.send(failResult);
            }
        }
    }
}