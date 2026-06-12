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
            log.info("주문 생성 요청 수신 message={}", message);

            event = objectMapper.readValue(message, OrderCreateRequestEvent.class);

            OrderCreateResultEvent result = orderService.createOrderFromEvent(event);

            orderCreateResultProducer.send(result);

            log.info("주문 생성 결과 발행 완료 requestId={}, orderId={}",
                    result.getRequestId(),
                    result.getOrderId()
            );

        } catch (Exception e) {
            String requestId = event != null ? event.getRequestId() : null;
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();

            log.error("주문 생성 처리 실패 requestId={}, rawMessage={}, error={}",
                    requestId,
                    message,
                    errorMessage,
                    e
            );

            if (requestId != null && !requestId.isBlank()) {
                OrderCreateResultEvent failResult = OrderCreateResultEvent.builder()
                        .requestId(requestId)
                        .orderId(null)
                        .orderPrice(null)
                        .createdAt(null)
                        .success(false)
                        .message(errorMessage)
                        .build();

                orderCreateResultProducer.send(failResult);
            }
        }
    }
}