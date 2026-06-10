package com.example.CafeAutoSystem.LLM.service;

import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import com.example.CafeAutoSystem.ingredient.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OllamaService {

    private final WebClient webClient;
    private final IngredientService ingredientService;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            너는 카페 재고관리 시스템의 '식자재 등록 비서'다.
            사용자의 말을 분석해서 아래 형식의 JSON 하나만 출력한다. JSON 외의 설명·인사·코드블록은 절대 쓰지 않는다.
            
            [출력 형식 — 셋 중 하나]
            1) 등록 요청 + 정보 충분:
               {"action":"register","ingredientName":"이름","unit":"단위","safetyStock":정수}
            2) 등록 요청 + 정보 부족:
               {"action":"ask","reply":"무엇이 더 필요한지 한국어 질문"}
            3) 등록과 무관한 일반 대화:
               {"action":"chat","reply":"한국어 답변"}
            
            [규칙]
            - unit 은 반드시 "ml", "g", "개", "pack" 중 하나만 쓴다. 사용자가 박스·통·캔·병·봉 등 다른 단위를 말하면 그대로 쓰지 말고, action 을 "ask" 로 해서 ml/g/개/pack 중 하나로 다시 물어본다.
            - 사용자가 명시적으로 말한 값만 쓴다. 값을 추측하거나 지어내지 않는다.
            - ingredientName, unit, safetyStock 중 하나라도 사용자가 말하지 않았으면 반드시 action 을 "ask" 로 한다.
            - safetyStock 은 정수만 쓴다.
            - 이 비서는 '식자재 등록 전용'이다. 등록과 무관한 질문(잡담·상식·날씨 등)에는 억지로 답하려 하지 말고, 식자재 등록을 돕겠다고 정중히 안내한다.
            
            [예시]
            입력: "우유 ml 안전재고 12"
            출력: {"action":"register","ingredientName":"우유","unit":"ml","safetyStock":12}
            입력: "원두 g로 등록"
            출력: {"action":"ask","reply":"원두의 안전재고 수량을 알려주세요."}
            입력: "우유"
            출력: {"action":"ask","reply":"우유의 단위(ml/g/개/pack)와 안전재고를 알려주세요."}
            입력: "콜라 1박스 안전재고 3 등록"
            출력: {"action":"ask","reply":"단위는 ml/g/개/pack 중 하나로 알려주세요. (박스는 지원하지 않아요)"}
            입력: "안녕"
            출력: {"action":"chat","reply":"안녕하세요! 등록할 식자재를 알려주시면 도와드릴게요."}
            입력: "코딩이 뭐야?"
            출력: {"action":"chat","reply":"저는 카페 식자재 등록을 돕는 비서예요. 그 질문엔 답하기 어렵고, 식자재명·단위·안전재고를 알려주시면 등록해드릴게요."}
            입력: "김 맛있어?"
            출력: {"action":"chat","reply":"맛 평가는 어렵지만, '김'을 식자재로 등록하시려면 단위와 안전재고를 알려주세요."}
            입력: "오늘 날씨 어때?"
            출력: {"action":"chat","reply":"저는 식자재 등록 도우미라 날씨는 알려드리기 어려워요. 등록할 식자재가 있나요?"}
            """;

    @SuppressWarnings("unchecked")
    public String chat(String message) {

        // 1) Ollama /api/chat 요청 본문 구성
        Map<String, Object> requestBody = Map.of(
                "model", "qwen3:4b",
                "stream", false,        // 한 번에 전체 응답
                "think", false,         // '생각 모드' OFF
                "format", "json",       // 출력을 항상 '유효한 JSON'으로 강제
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", message)
                ),
                "options", Map.of("num_ctx", 2048, "temperature", 0)   // 컨텍스트 축소 + 창의성 0(헛값 방지)
        );


        // 2) WebClient 로 POST 호출 (MVC라 block 으로 동기 수신)
        Map<String, Object> response = webClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()// 응답 시작점
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block(Duration.ofSeconds(120));      // LLM 느릴 수 있어 타임아웃 넉넉히

        // 3) 응답에서 message.content 추출
        if (response == null) return "";
        Map<String, Object> msg = (Map<String, Object>) response.get("message");
        String content = (msg != null && msg.get("content") != null)
                ? msg.get("content").toString() : "";

        // 4) 분기
        JsonNode node = objectMapper.readTree(content);
        String action = node.path("action").asString();
        String ingredientName = node.path("ingredientName").asString();
        String unit = node.path("unit").asString();
        int safetyStock = node.path("safetyStock").asInt();

        // register , chat , ask
        if (action.equals("register")) {
            IngredientDto dto = IngredientDto.builder()
                    .ingredientName(ingredientName)
                    .unit(unit)
                    .safetyStock(safetyStock)
                    .build();
            try {
                ingredientService.create(dto);
                return ingredientName + " 등록 완료했어요!";
            } catch (Exception e) {
                return "등록 실패: " + e.getMessage();
            }
        }
        return node.path("reply").asString();
    }
}
