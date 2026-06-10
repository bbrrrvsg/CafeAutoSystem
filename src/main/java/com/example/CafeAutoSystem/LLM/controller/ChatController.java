package com.example.CafeAutoSystem.LLM.controller;

import com.example.CafeAutoSystem.LLM.dto.ChatRequest;
import com.example.CafeAutoSystem.LLM.dto.ChatResponse;
import com.example.CafeAutoSystem.LLM.service.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final OllamaService ollamaService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = ollamaService.chat(request.getMessage());
        return ChatResponse.builder()
                .answer(answer)
                .build();
    }
}
