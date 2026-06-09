package com.example.CafeAutoSystem.global.outbox;

import com.example.CafeAutoSystem.global.event.EventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 도메인 서비스에서 outbox 이벤트를 저장할 때 사용하는 서비스.
 *
 * 반드시 도메인 저장 트랜잭션 안에서 호출되어야 한다.
 */
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void saveEvent(
            String topic,
            String eventType,
            String aggregateType,
            String aggregateId,
            JsonNode payload
    ) {
        try {
            String eventId = UUID.randomUUID().toString();

            EventEnvelope envelope = EventEnvelope.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .eventVersion(1)
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .occurredAt(LocalDateTime.now().toString())
                    .payload(payload)
                    .build();

            String envelopeJson = objectMapper.writeValueAsString(envelope);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(eventId)
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .topic(topic)
                    .kafkaKey(aggregateId)
                    .payload(envelopeJson)
                    .status("NEW")
                    .retryCount(0)
                    .build();

            outboxRepository.save(outboxEvent);

        } catch (Exception e) {
            throw new RuntimeException("outbox 이벤트 저장 실패", e);
        }
    }
}