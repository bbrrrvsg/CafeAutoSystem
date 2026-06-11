package com.example.CafeAutoSystem.LLM.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 로컬: 기본 localhost / AWS 배포: application.properties 에 ollama.url=<ngrok주소> 추가
    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(ollamaUrl)
                .defaultHeader("ngrok-skip-browser-warning", "true") // ngrok 경고페이지 우회 (localhost면 무해)
                .build();
    }
}
