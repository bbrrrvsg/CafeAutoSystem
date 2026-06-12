package com.example.CafeAutoSystem.LLM.service;

/**
 * Ollama(LLM) 서버에 연결할 수 없을 때만 발생.
 * ChatbotService 가 이 예외만 잡아 정형 챗봇으로 fallback 한다.
 * (그 외 일반 오류는 fallback 하지 않고 그대로 노출 → 버그가 정형으로 숨지 않음)
 */
public class LlmUnavailableException extends RuntimeException {
    public LlmUnavailableException(Throwable cause) {
        super("LLM 서버에 연결할 수 없습니다.", cause);
    }
}
