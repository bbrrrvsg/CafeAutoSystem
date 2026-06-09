package com.example.CafeAutoSystem.global.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * outbox NEW 이벤트를 Kafka로 발행하는 릴레이.
 *
 * MVP에서는 polling scheduler 방식으로 충분하다.
 * 운영 구조에서는 Debezium CDC 기반 outbox로 확장 가능하다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishNewEvents() {
        List<OutboxEvent> events = outboxRepository.findTop50ByStatusOrderByIdAsc("NEW");

        if (events.isEmpty()) {
            return;
        }

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        event.getTopic(),
                        event.getKafkaKey(),
                        event.getPayload()
                ).get();

                event.markSent();

                log.info("✅ 사장 서버 outbox 이벤트 발행 성공 eventId={}, topic={}",
                        event.getEventId(), event.getTopic());

            } catch (Exception e) {
                event.markFailed(
                        e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()
                );

                log.error("❌ 사장 서버 outbox 이벤트 발행 실패 eventId={}, topic={}",
                        event.getEventId(), event.getTopic(), e);
            }
        }
    }
}