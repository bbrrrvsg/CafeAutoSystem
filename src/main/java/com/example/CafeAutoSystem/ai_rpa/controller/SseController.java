package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.service.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    /**
     * [GET] 클라이언트가 SSE 연결 요청
     * inventory.jsp 에서 new EventSource('/api/sse/stock') 로 연결
     */
    @GetMapping(value = "/stock", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeStock() {
        return sseEmitterManager.register();
    }
}