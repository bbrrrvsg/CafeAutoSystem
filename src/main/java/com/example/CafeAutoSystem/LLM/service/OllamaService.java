package com.example.CafeAutoSystem.LLM.service;

import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import com.example.CafeAutoSystem.ingredient.service.IngredientService;
import com.example.CafeAutoSystem.stock.dto.InventoryResponse;
import com.example.CafeAutoSystem.stock.service.InventoryService;
import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import com.example.CafeAutoSystem.vendor.service.VendorService;
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
    private final ObjectMapper objectMapper;
    private final IngredientService ingredientService;
    private final VendorService vendorService;
    private final InventoryService inventoryService;

    // 테스트 시 모델 교체 쉽게 — application.properties 의 ollama.model, 없으면 qwen3:4b
    @Value("${ollama.model:qwen3:4b}")
    private String model;

    // 응답 타임아웃(초). 테스트 시 짧게(예: 1) 주면 LLM이 그 안에 응답 못 할 때 정형으로 빨리 fallback
    @Value("${ollama.timeout-seconds:120}")
    private long timeoutSeconds;

    private static final String SYSTEM_PROMPT = """
            너는 카페 재고관리 시스템의 비서다.
            사용자의 말을 분석해서 아래 JSON 하나만 출력한다. JSON 외의 설명·인사·코드블록은 절대 쓰지 않는다.

            [출력 형식 — 다섯 중 하나]
            1) 식자재 등록: {"action":"register","target":"ingredient","ingredientName":"이름","unit":"단위","safetyStock":정수}
            2) 거래처 등록: {"action":"register","target":"vendor","vendorName":"이름","managerEmail":"이메일","managerPhone":"연락처"}
            3) 조회:
               · 특정 식자재 재고: {"action":"query","target":"stock","ingredientName":"이름"}
               · 부족한 재고 목록: {"action":"query","target":"lowstock"}
               · 거래처 목록:     {"action":"query","target":"vendors"}
               · 식자재 목록:     {"action":"query","target":"ingredients"}
            4) 정보 부족:   {"action":"ask","reply":"무엇이 더 필요한지 한국어 질문"}
            5) 일반 대화:   {"action":"chat","reply":"한국어 답변"}

            [규칙]
            - target 은 "ingredient"(식자재) 또는 "vendor"(거래처) 중 하나.
            - unit 은 반드시 "ml","g","개","pack" 중 하나. 사용자가 박스·통·캔·병·봉 등을 말하면 그대로 쓰지 말고 action 을 "ask" 로.
            - 사용자가 명시적으로 말한 값만 쓴다. 추측·창작 금지.
            - 등록에 필요한 값이 하나라도 없으면 반드시 action 을 "ask" 로 한다.
              · 식자재: ingredientName, unit, safetyStock
              · 거래처: vendorName, managerEmail, managerPhone
            - safetyStock 은 정수만.
            - 등록·조회와 무관한 잡담(상식·날씨 등)에는 억지로 답하지 말고 도와줄 일을 안내한다.

            [예시]
            입력: "우유 ml 안전재고 12 등록"
            출력: {"action":"register","target":"ingredient","ingredientName":"우유","unit":"ml","safetyStock":12}
            입력: "거래처 서울유통 이메일 a@b.com 연락처 010-1111-2222 등록"
            출력: {"action":"register","target":"vendor","vendorName":"서울유통","managerEmail":"a@b.com","managerPhone":"010-1111-2222"}
            입력: "우유 재고 얼마야?"
            출력: {"action":"query","target":"stock","ingredientName":"우유"}
            입력: "재고 부족한 거 뭐야?"
            출력: {"action":"query","target":"lowstock"}
            입력: "거래처 목록 보여줘"
            출력: {"action":"query","target":"vendors"}
            입력: "식자재 뭐 있어?"
            출력: {"action":"query","target":"ingredients"}
            입력: "거래처 등록해줘"
            출력: {"action":"ask","reply":"거래처명, 담당자 이메일, 연락처를 알려주세요."}
            입력: "우유"
            출력: {"action":"ask","reply":"우유의 단위(ml/g/개/pack)와 안전재고를 알려주세요."}
            입력: "콜라 1박스 안전재고 3 등록"
            출력: {"action":"ask","reply":"단위는 ml/g/개/pack 중 하나로 알려주세요. (박스는 지원하지 않아요)"}
            입력: "안녕"
            출력: {"action":"chat","reply":"안녕하세요! 식자재·거래처 등록이나 재고 조회를 도와드릴게요."}
            입력: "오늘 날씨 어때?"
            출력: {"action":"chat","reply":"저는 재고관리 비서라 날씨는 어려워요. 등록이나 재고 조회를 도와드릴까요?"}
            """;

    @SuppressWarnings("unchecked")
    public String chat(String message) {
        // 1) Ollama /api/chat 호출
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "stream", false,
                "think", false,
                "format", "json",
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", message)
                ),
                "options", Map.of("num_ctx", 2048, "temperature", 0)
        );

        Map<String, Object> response;
        try {
            response = webClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(timeoutSeconds));
        } catch (Exception e) {
            // Ollama 미실행/연결실패/타임아웃 → 전용 예외 → ChatbotService 가 정형 챗봇으로 fallback
            throw new LlmUnavailableException(e);
        }

        if (response == null) return "LLM 응답이 없어요.";
        Map<String, Object> msg = (Map<String, Object>) response.get("message");
        String content = (msg != null && msg.get("content") != null) ? msg.get("content").toString() : "";

        // 2) JSON 파싱 → action/target 으로 분기
        try {
            JsonNode node = objectMapper.readTree(content);
            String action = node.path("action").asString();
            String target = node.path("target").asString();

            if ("register".equals(action)) {
                return "vendor".equals(target) ? registerVendor(node) : registerIngredient(node);
            }
            if ("query".equals(action)) {
                return switch (target) {
                    case "lowstock"    -> queryLowStock();
                    case "vendors"     -> queryVendors();
                    case "ingredients" -> queryIngredients();
                    default            -> queryStock(node.path("ingredientName").asString());
                };
            }
            // ask, chat
            return node.path("reply").asString();
        } catch (Exception e) {
            return "처리 중 오류가 났어요: " + e.getMessage();
        }
    }

    // ===== 등록 =====
    private String registerIngredient(JsonNode node) {
        try {
            IngredientDto dto = IngredientDto.builder()
                    .ingredientName(node.path("ingredientName").asString())
                    .unit(node.path("unit").asString())
                    .safetyStock(node.path("safetyStock").asInt())
                    .build();
            ingredientService.create(dto);
            return dto.getIngredientName() + " 식자재를 등록했어요!";
        } catch (Exception e) {
            return "식자재 등록 실패: " + e.getMessage();
        }
    }

    private String registerVendor(JsonNode node) {
        try {
            VendorDto dto = VendorDto.builder()
                    .vendorName(node.path("vendorName").asString())
                    .managerEmail(node.path("managerEmail").asString())
                    .managerPhone(node.path("managerPhone").asString())
                    .build();
            vendorService.create(dto);
            return dto.getVendorName() + " 거래처를 등록했어요!";
        } catch (Exception e) {
            return "거래처 등록 실패: " + e.getMessage();
        }
    }

    // ===== 조회 =====
    private String queryStock(String name) {
        if (name == null || name.isBlank()) return "어떤 식자재의 재고를 알려드릴까요?";
        for (InventoryResponse inv : inventoryService.getInventoryList()) {
            if (inv.getIngredientName() != null && inv.getIngredientName().contains(name)) {
                return inv.getIngredientName() + " 재고: " + inv.getCurrentStock() + inv.getUnit()
                        + " (안전재고 " + inv.getSafetyStock() + inv.getUnit() + ", 상태 " + inv.getStatus() + ")";
            }
        }
        return "'" + name + "' 식자재를 찾지 못했어요.";
    }

    private String queryLowStock() {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (InventoryResponse i : inventoryService.getInventoryList()) {
            if ("LOW".equals(i.getStatus())) {
                sb.append("· ").append(i.getIngredientName()).append(" ")
                  .append(i.getCurrentStock()).append(i.getUnit())
                  .append(" (안전 ").append(i.getSafetyStock()).append(i.getUnit()).append(")\n");
                n++;
            }
        }
        if (n == 0) return "부족한 재고는 없어요. 👍";
        return "재고 부족 품목 " + n + "개:\n" + sb.toString().trim();
    }

    private String queryVendors() {
        var vendors = vendorService.getAll();
        if (vendors.isEmpty()) return "등록된 거래처가 없어요.";
        StringBuilder sb = new StringBuilder("거래처 " + vendors.size() + "곳:\n");
        for (VendorDto v : vendors) {
            sb.append("· ").append(v.getVendorName());
            if (v.getManagerPhone() != null && !v.getManagerPhone().isBlank()) {
                sb.append(" (").append(v.getManagerPhone()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private String queryIngredients() {
        var ings = ingredientService.getAll();
        if (ings.isEmpty()) return "등록된 식자재가 없어요.";
        StringBuilder sb = new StringBuilder("식자재 " + ings.size() + "개:\n");
        for (IngredientDto i : ings) {
            sb.append("· ").append(i.getIngredientName()).append(" (").append(i.getUnit()).append(")\n");
        }
        return sb.toString().trim();
    }
}
