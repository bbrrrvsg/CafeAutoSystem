package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewListPageResponseDto;
import com.example.CafeAutoSystem.reply.dto.ReviewListQueryRequestEvent;
import com.example.CafeAutoSystem.reply.dto.ReviewListQueryResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewListKafkaClient {

    private final ConcurrentHashMap<String, CompletableFuture<ReviewListQueryResultEvent>> pendingRequests
            = new ConcurrentHashMap<>();

    private final ReviewListQueryRequestProducer producer;

    public ReviewListPageResponseDto requestReviews(int page, int size) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<ReviewListQueryResultEvent> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            ReviewListQueryRequestEvent event = new ReviewListQueryRequestEvent(requestId, page, size);
            producer.send(event);

            ReviewListQueryResultEvent result = future.get(10, TimeUnit.SECONDS);
            return ReviewListPageResponseDto.from(result);

        } catch (TimeoutException e) {
            log.error("리뷰 목록 조회 타임아웃 requestId={}", requestId);
            throw new RuntimeException("리뷰 목록 조회 요청이 시간 초과되었습니다.");
        } catch (Exception e) {
            log.error("리뷰 목록 조회 실패 requestId={}", requestId, e);
            throw new RuntimeException("리뷰 목록 조회 중 오류가 발생했습니다.");
        } finally {
            pendingRequests.remove(requestId);
        }
    }

    public void complete(ReviewListQueryResultEvent result) {
        CompletableFuture<ReviewListQueryResultEvent> future = pendingRequests.get(result.getRequestId());
        if (future != null) {
            future.complete(result);
        } else {
            log.warn("매칭되는 요청 없음 requestId={}", result.getRequestId());
        }
    }
}
