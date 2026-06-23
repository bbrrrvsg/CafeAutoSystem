package com.example.CafeAutoSystem.LLM.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * 챗봇 LLM 통신 계층 (Spring AI ChatClient).
 * 사용자 메시지를 LLM 에 보내고, LLM 이 필요 시 CafeTools 의 @Tool 을 호출(tool calling)하도록 한다.
 * 등록/조회 같은 실제 동작은 CafeTools 가 담당하고, 여기서는 통신만 한다.
 * 연결 실패 시 LlmUnavailableException 을 던져 ChatbotService 가 정형 챗봇으로 fallback.
 */
@Service
@RequiredArgsConstructor
public class OllamaService {

    private final ChatClient chatClient;
    private final CafeTools cafeTools;

    private static final String SYSTEM_PROMPT = """
            너는 카페 재고관리 시스템의 한국어 비서다.
            식자재·거래처 등록과 재고·거래처 조회는 반드시 제공된 도구(tool)를 사용해서 처리한다.

            [규칙]
            - 단위(unit)는 ml/g/개/pack 중 하나로 넘긴다. '팩'은 pack, '그램'은 g, '밀리리터'는 ml 로 본다.
              박스·통·캔·병 등 변환 불가한 단위면 도구를 호출하지 말고 지원 단위로 되묻는다.
            - 등록에 필요한 값이 하나라도 빠졌으면 도구를 호출하지 말고, 무엇이 필요한지 한국어로 되묻는다.
              · 식자재: 식자재명, 단위, 안전재고
              · 거래처: 거래처명, 담당자 이메일, 담당자 연락처
            - 사용자가 명시적으로 말한 값만 쓴다. 단위·안전재고·이메일·연락처 등을 사용자가 말하지 않았으면
              절대 임의로 지어내지 말고, 도구를 호출하지 말고 한국어로 되묻는다.
            - 재고관리와 무관한 잡담(상식·날씨 등)에는 억지로 답하지 말고 도와줄 일을 정중히 안내한다.
            - 도구를 호출한 뒤에는 반드시 그 도구가 돌려준 결과 메시지를 사용자에게 한국어로 알려준다.
              절대 빈 응답을 내지 않는다.
            - 답변은 간결한 한국어로 한다.
            """;

    public String chat(String message) {
        CafeTools.clearLastResult();   // 이전 요청 잔여값 제거
        try {
            String answer = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(message)
                    .tools(cafeTools)        // ← LLM 이 호출할 수 있는 도구 등록
                    .call()
                    .content();

            String toolResult = CafeTools.consumeLastResult();
            if (answer != null && !answer.isBlank()) {
                return answer;                       // 모델이 정상 답변 생성
            }
            if (toolResult != null) {
                return toolResult;                   // 도구는 실행됐는데 모델이 멘트를 빼먹음 → 도구 결과로 대체
            }
            // 연결은 됐지만(=서버 정상) 모델이 모호한 입력을 이해 못 해 빈 응답을 냄.
            // 서버 장애가 아니므로 간편 모드로 떨어뜨리지 말고, AI 모드에서 무엇을 원하는지 되묻는다.
            return "무엇을 도와드릴까요? 예) \"우유 재고\"(조회) · \"우유 개 안전재고 10 등록\"(등록) 처럼 말씀해 주세요.";
        } catch (Exception e) {
            // Ollama 미실행/연결실패/타임아웃 → ChatbotService 가 정형 챗봇으로 fallback
            throw new LlmUnavailableException(e);
        }
    }
}
