package com.example.CafeAutoSystem.LLM.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 챗봇 진입점.
 * 1) 진행 중인 정형(단계별) 대화가 있으면 그걸 이어간다.
 * 2) 아니면 LLM(Ollama)을 먼저 시도하고, LLM 연결이 안 되면 정형 챗봇으로 자동 fallback.
 *    → 로컬 PC(Ollama)가 꺼져도 AWS 배포 서버는 정형 챗봇으로 계속 동작.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final OllamaService ollamaService;
    private final RuleChatService ruleChatService;

    public String chat(String message, HttpSession session) {
        // 진행 중인 정형 대화가 있으면 끊지 않고 계속
        if (session.getAttribute(RuleChatService.SESSION_KEY) != null) {
            log.info("[챗봇] 정형 대화 진행 중 → 정형 모드 유지");
            return ruleChatService.handle(message, session);
        }
        // LLM 우선 → '연결 실패'일 때만 정형 챗봇으로 fallback
        try {
            String answer = ollamaService.chat(message);
            log.info("[챗봇] LLM(Ollama) 모드로 응답");
            return answer;
        } catch (LlmUnavailableException e) {
            log.warn("[챗봇] ⚠️ LLM 연결 실패 → 정형 챗봇으로 fallback (사유: {})",
                    e.getCause() != null ? e.getCause().getClass().getSimpleName() : "unknown");
            return ruleChatService.handle(message, session);
        }
    }
}
