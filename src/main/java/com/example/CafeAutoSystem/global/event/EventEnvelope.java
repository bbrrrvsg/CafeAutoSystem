package com.example.CafeAutoSystem.global.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

/**
 * Kafka 도메인 이벤트 공통 포맷.
 *
 * Kafka에는 query-request/result가 아니라
 * 이미 발생한 사실(event)만 흘려보낸다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope {

    private String eventId;

    private String eventType;

    private Integer eventVersion;

    private String aggregateType;

    private String aggregateId;

    private String occurredAt;

    private JsonNode payload;
}