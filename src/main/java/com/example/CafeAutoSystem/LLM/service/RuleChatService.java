package com.example.CafeAutoSystem.LLM.service;

import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import com.example.CafeAutoSystem.ingredient.service.IngredientService;
import com.example.CafeAutoSystem.stock.dto.InventoryResponse;
import com.example.CafeAutoSystem.stock.service.InventoryService;
import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import com.example.CafeAutoSystem.vendor.service.VendorService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 정형(규칙기반) 챗봇 — LLM 없이 동작.
 * 단계별 질문으로 빈칸(slot)을 채워 등록하며, 진행상태는 HttpSession 에 저장(멀티턴).
 */
@Service
@RequiredArgsConstructor
public class RuleChatService {

    public static final String SESSION_KEY = "ruleConv";

    private final IngredientService ingredientService;
    private final VendorService vendorService;
    private final InventoryService inventoryService;

    private static final Set<String> UNITS = Set.of("ml", "g", "개", "pack");

    // 의도별 필요 필드 (이 순서대로 물어봄). 대화맵의 "__intent" 키로 현재 의도 보관.
    private static final Map<String, String[]> FIELDS = Map.of(
            "ingredient", new String[]{"ingredientName", "unit", "safetyStock"},
            "vendor",     new String[]{"vendorName", "managerEmail", "managerPhone"}
    );

    @SuppressWarnings("unchecked")
    public String handle(String message, HttpSession session) {
        String msg = (message == null) ? "" : message.trim();
        Map<String, String> conv = (Map<String, String>) session.getAttribute(SESSION_KEY);

        // 취소
        if (msg.equals("취소") || msg.equals("그만")) {
            session.removeAttribute(SESSION_KEY);
            return "진행을 취소했어요.";
        }

        // 진행 중인 대화 없음 → 의도 감지
        if (conv == null) {
            if (msg.contains("식자재") && msg.contains("등록")) return startFlow("ingredient", session);
            if (msg.contains("거래처") && msg.contains("등록")) return startFlow("vendor", session);
            if (msg.contains("부족")) return queryLowStock();
            if (msg.contains("재고")) return queryStock(msg);
            return help();
        }

        // 진행 중 → 지금 묻던 필드에 값 채우기
        String field = nextMissing(conv);
        if (field != null) {
            String err = setSlot(conv, field, msg);
            if (err != null) return err;   // 잘못된 값 → 같은 질문 다시
        }

        // 다음 빈 필드 묻기 or 완료 시 실행
        String next = nextMissing(conv);
        if (next != null) return ask(next);
        return execute(conv, session);
    }

    private String startFlow(String intent, HttpSession session) {
        Map<String, String> conv = new LinkedHashMap<>();
        conv.put("__intent", intent);
        session.setAttribute(SESSION_KEY, conv);
        String head = "ingredient".equals(intent) ? "식자재 등록을 시작할게요.\n" : "거래처 등록을 시작할게요.\n";
        return head + ask(FIELDS.get(intent)[0]) + "\n(중단하려면 '취소')";
    }

    private String nextMissing(Map<String, String> conv) {
        for (String f : FIELDS.get(conv.get("__intent"))) {
            if (!conv.containsKey(f)) return f;
        }
        return null;
    }

    private String setSlot(Map<String, String> conv, String field, String value) {
        if ("unit".equals(field) && !UNITS.contains(value)) {
            return "단위는 ml / g / 개 / pack 중 하나로 입력해주세요.";
        }
        if ("safetyStock".equals(field) && !value.matches("\\d+")) {
            return "안전재고는 숫자로 입력해주세요. (예: 30)";
        }
        conv.put(field, value);
        return null;
    }

    private String ask(String field) {
        return switch (field) {
            case "ingredientName" -> "제품명을 알려주세요.";
            case "unit"           -> "단위는요? (ml / g / 개 / pack)";
            case "safetyStock"    -> "안전재고 수량을 알려주세요. (숫자)";
            case "vendorName"     -> "거래처명을 알려주세요.";
            case "managerEmail"   -> "담당자 이메일을 알려주세요.";
            case "managerPhone"   -> "담당자 연락처를 알려주세요.";
            default               -> "값을 알려주세요.";
        };
    }

    private String execute(Map<String, String> conv, HttpSession session) {
        String intent = conv.get("__intent");
        session.removeAttribute(SESSION_KEY);
        try {
            if ("ingredient".equals(intent)) {
                ingredientService.create(IngredientDto.builder()
                        .ingredientName(conv.get("ingredientName"))
                        .unit(conv.get("unit"))
                        .safetyStock(Integer.parseInt(conv.get("safetyStock")))
                        .build());
                return conv.get("ingredientName") + " 식자재를 등록했어요! ✅";
            } else {
                vendorService.create(VendorDto.builder()
                        .vendorName(conv.get("vendorName"))
                        .managerEmail(conv.get("managerEmail"))
                        .managerPhone(conv.get("managerPhone"))
                        .build());
                return conv.get("vendorName") + " 거래처를 등록했어요! ✅";
            }
        } catch (Exception e) {
            return "등록 실패: " + e.getMessage();
        }
    }

    // ===== 간단 조회 =====
    private String queryStock(String msg) {
        for (InventoryResponse inv : inventoryService.getInventoryList()) {
            if (inv.getIngredientName() != null && msg.contains(inv.getIngredientName())) {
                return inv.getIngredientName() + " 재고: " + inv.getCurrentStock() + inv.getUnit()
                        + " (상태 " + inv.getStatus() + ")";
            }
        }
        return "어떤 식자재의 재고인지 알려주세요. (예: '우유 재고')";
    }

    private String queryLowStock() {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (InventoryResponse i : inventoryService.getInventoryList()) {
            if ("LOW".equals(i.getStatus())) {
                sb.append("· ").append(i.getIngredientName()).append(" ")
                  .append(i.getCurrentStock()).append(i.getUnit()).append("\n");
                n++;
            }
        }
        return (n == 0) ? "부족한 재고는 없어요. 👍" : "재고 부족 " + n + "개:\n" + sb.toString().trim();
    }

    private String help() {
        return """
                안녕하세요! (간편 모드 — AI 서버 미연결)
                이렇게 말해보세요:
                · "식자재 등록" → 단계별로 안내해드려요
                · "거래처 등록"
                · "우유 재고" → 재고 조회
                · "부족" → 부족한 재고 보기""";
    }
}
