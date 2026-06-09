package com.example.CafeAutoSystem.LLM.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    // TODO: 요청 필드 정의 (예: message)
    private String message;

}
