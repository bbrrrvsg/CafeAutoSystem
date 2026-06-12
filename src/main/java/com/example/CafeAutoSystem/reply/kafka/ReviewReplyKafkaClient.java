package com.example.CafeAutoSystem.reply.kafka;

import com.example.CafeAutoSystem.reply.dto.ReviewReplyCommandRequestEvent;
import com.example.CafeAutoSystem.reply.dto.ReviewReplyCommandResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReviewReplyKafkaClient {

    private final ReviewReplyCommandRequestProducer requestProducer;

    private final Map<String, CompletableFuture<ReviewReplyCommandResultEvent>> pendingRequests =
            new ConcurrentHashMap<>();

    public ReviewReplyCommandResultEvent requestReplyCommand(
            ReviewReplyCommandRequestEvent event
    ) {
        CompletableFuture<ReviewReplyCommandResultEvent> future =
                new CompletableFuture<>();

        pendingRequests.put(event.getRequestId(), future);

        try {
            requestProducer.send(event);

            return future.get(15, TimeUnit.SECONDS);

        } catch (Exception e) {
            throw new RuntimeException("답글 명령 결과 수신 실패", e);

        } finally {
            pendingRequests.remove(event.getRequestId());
        }
    }

    public void complete(ReviewReplyCommandResultEvent resultEvent) {
        if (resultEvent == null || resultEvent.getRequestId() == null) {
            return;
        }

        CompletableFuture<ReviewReplyCommandResultEvent> future =
                pendingRequests.remove(resultEvent.getRequestId());

        if (future == null) {
            return;
        }

        if (Boolean.TRUE.equals(resultEvent.getSuccess())) {
            future.complete(resultEvent);
            return;
        }

        future.completeExceptionally(
                new RuntimeException(
                        resultEvent.getMessage() != null
                                ? resultEvent.getMessage()
                                : "답글 명령 처리에 실패했습니다."
                )
        );
    }

    public String createRequestId() {
        return UUID.randomUUID().toString();
    }
}