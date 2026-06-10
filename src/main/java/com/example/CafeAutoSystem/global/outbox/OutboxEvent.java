package com.example.CafeAutoSystem.global.outbox;

import com.example.CafeAutoSystem.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사장 서버의 도메인 변경 이벤트를 저장하는 outbox 테이블.
 *
 * 도메인 저장과 outbox 저장을 같은 트랜잭션으로 묶어서
 * DB 저장 성공 후 Kafka 이벤트 유실 문제를 줄인다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "outbox",
        indexes = {
                @Index(name = "idx_outbox_status_id", columnList = "status, id"),
                @Index(name = "idx_outbox_event_type", columnList = "event_type"),
                @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
        }
)
public class OutboxEvent extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 100)
    private String topic;

    @Column(name = "kafka_key", nullable = false, length = 100)
    private String kafkaKey;

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Lob
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    /**
     * Kafka 발행 성공 시각.
     * created_at / updated_at은 BaseTime이 관리하고,
     * sent_at만 OutboxEvent가 별도로 관리한다.
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    public void prePersistOutbox() {
        if (status == null) {
            status = "NEW";
        }

        if (retryCount == null) {
            retryCount = 0;
        }
    }

    public void markSent() {
        this.status = "SENT";
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }

    public void markFailed(String errorMessage) {
        this.retryCount = this.retryCount == null ? 1 : this.retryCount + 1;
        this.lastError = errorMessage;
    }
}