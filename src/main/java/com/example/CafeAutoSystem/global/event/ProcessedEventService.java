package com.example.CafeAutoSystem.global.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * 컨슈머 중복 처리를 막는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ProcessedEventService {

    private final ProcessedEventRepository processedEventRepository;

    public boolean tryMarkProcessed(String eventId, String eventType) {
        try {
            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .eventId(eventId)
                            .eventType(eventType)
                            .build()
            );
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}