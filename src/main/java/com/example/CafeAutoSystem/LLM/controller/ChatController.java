package com.example.CafeAutoSystem.LLM.controller;

import com.example.CafeAutoSystem.LLM.dto.ChatRequest;
import com.example.CafeAutoSystem.LLM.dto.ChatResponse;
import com.example.CafeAutoSystem.LLM.service.ChatbotService;
import com.example.CafeAutoSystem.LLM.service.RuleChatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request, HttpSession session) {
        String answer = chatbotService.chat(request.getMessage(), session);
        return ChatResponse.builder().answer(answer).build();
    }

    // 클라이언트 "지우기" 버튼이 호출 — 서버 세션의 진행 중 정형 흐름도 함께 폐기
    @DeleteMapping
    public ChatResponse reset(HttpSession session) {
        session.removeAttribute(RuleChatService.SESSION_KEY);
        return ChatResponse.builder().answer("ok").build();
    }
}
