package com.example.CafeAutoSystem.global.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Kafka 컨슈머 멱등 처리를 위한 테이블.
 *
 * 같은 eventId가 이미 처리되었으면 다시 처리하지 않는다.
 */
@Entity
@Table(
        name = "processed_event",
        indexes = {
                @Index(name = "idx_processed_event_type", columnList = "event_type")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    @Column(name = "event_id", length = 100)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}