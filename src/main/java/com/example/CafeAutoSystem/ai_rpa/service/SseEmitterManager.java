package com.example.CafeAutoSystem.ai_rpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class SseEmitterManager {

    // 현재 연결된 클라이언트(브라우저) 목록 - 동시성 안전한 리스트
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /** 새 클라이언트 연결 등록 */
    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 타임아웃 없음

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        emitters.add(emitter);
        log.info("[SSE] 클라이언트 연결 - 현재 {}명 접속 중", emitters.size());
        return emitter;
    }

    /** 주문 발생 시 모든 연결된 클라이언트에게 재고 갱신 이벤트 푸시 */
    public void broadcastStockUpdate() {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("stockUpdate")  // 이벤트 이름
                        .data("reload"));     // 데이터 (브라우저가 이걸 받으면 재고 다시 조회)
            } catch (IOException e) {
                deadEmitters.add(emitter);
                log.warn("[SSE] 끊어진 클라이언트 제거");
            }
        });

        emitters.removeAll(deadEmitters);
    }
}