package com.example.CafeAutoSystem.LLM.service;

import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import com.example.CafeAutoSystem.ingredient.service.IngredientService;
import com.example.CafeAutoSystem.stock.dto.InventoryResponse;
import com.example.CafeAutoSystem.stock.service.InventoryService;
import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import com.example.CafeAutoSystem.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 챗봇이 호출하는 도구(@Tool) 묶음.
 * LLM 이 사용자 말에서 적절한 도구를 골라 인자를 채워 호출하면 Spring AI 가 이 메서드를 실행한다.
 * 본문은 기존 도메인 Service 에 위임만 한다 (비즈니스 로직 중복 없음).
 */
@Component
@RequiredArgsConstructor
public class CafeTools {

    private static final Set<String> UNITS = Set.of("ml", "g", "개", "pack");

    // 도구가 실제로 실행됐을 때의 결과 메시지를 호출 스레드에 기록해 둠.
    // (작은 모델이 도구 실행 후 마무리 멘트를 빼먹어도 OllamaService 가 이 값을 대신 반환)
    private static final ThreadLocal<String> LAST_RESULT = new ThreadLocal<>();

    public static void clearLastResult() { LAST_RESULT.remove(); }
    public static String consumeLastResult() {
        String v = LAST_RESULT.get();
        LAST_RESULT.remove();
        return v;
    }
    private String record(String msg) { LAST_RESULT.set(msg); return msg; }

    private final IngredientService ingredientService;
    private final VendorService vendorService;
    private final InventoryService inventoryService;

    // ===== 등록 =====
    @Tool(description = "식자재(재료)를 등록한다. unit 은 ml/g/개/pack 중 하나만 허용된다.")
    public String registerIngredient(
            @ToolParam(description = "식자재명") String name,
            @ToolParam(description = "단위: ml, g, 개, pack 중 하나") String unit,
            @ToolParam(description = "안전재고 수량(정수)") int safetyStock) {
        if (!UNITS.contains(unit)) {
            return record("단위는 ml/g/개/pack 중 하나여야 해요. (입력값: " + unit + ")");
        }
        try {
            ingredientService.create(IngredientDto.builder()
                    .ingredientName(name)
                    .unit(unit)
                    .safetyStock(safetyStock)
                    .build());
            return record(name + " 식자재를 등록했어요!");
        } catch (Exception e) {
            return record("식자재 등록 실패: " + e.getMessage());
        }
    }

    @Tool(description = "거래처를 등록한다.")
    public String registerVendor(
            @ToolParam(description = "거래처명") String name,
            @ToolParam(description = "담당자 이메일") String email,
            @ToolParam(description = "담당자 연락처") String phone) {
        try {
            vendorService.create(VendorDto.builder()
                    .vendorName(name)
                    .managerEmail(email)
                    .managerPhone(phone)
                    .build());
            return record(name + " 거래처를 등록했어요!");
        } catch (Exception e) {
            return record("거래처 등록 실패: " + e.getMessage());
        }
    }

    // ===== 조회 =====
    @Tool(description = "특정 식자재의 현재고/안전재고/상태를 조회한다.")
    public String getStock(@ToolParam(description = "식자재명") String name) {
        if (name == null || name.isBlank()) return record("어떤 식자재의 재고를 알려드릴까요?");
        for (InventoryResponse inv : inventoryService.getInventoryList()) {
            if (inv.getIngredientName() != null && inv.getIngredientName().contains(name)) {
                return record(inv.getIngredientName() + " 재고: " + inv.getCurrentStock() + inv.getUnit()
                        + " (안전재고 " + inv.getSafetyStock() + inv.getUnit() + ", 상태 " + inv.getStatus() + ")");
            }
        }
        return record("'" + name + "' 식자재를 찾지 못했어요.");
    }

    @Tool(description = "안전재고 미만(LOW)인, 즉 부족한 식자재 목록을 조회한다.")
    public String getLowStock() {
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
        if (n == 0) return record("부족한 재고는 없어요. 👍");
        return record("재고 부족 품목 " + n + "개:\n" + sb.toString().trim());
    }

    @Tool(description = "등록된 거래처 목록을 조회한다.")
    public String listVendors() {
        var vendors = vendorService.getAll();
        if (vendors.isEmpty()) return record("등록된 거래처가 없어요.");
        StringBuilder sb = new StringBuilder("거래처 " + vendors.size() + "곳:\n");
        for (VendorDto v : vendors) {
            sb.append("· ").append(v.getVendorName());
            if (v.getManagerPhone() != null && !v.getManagerPhone().isBlank()) {
                sb.append(" (").append(v.getManagerPhone()).append(")");
            }
            sb.append("\n");
        }
        return record(sb.toString().trim());
    }

    @Tool(description = "등록된 식자재 목록을 조회한다.")
    public String listIngredients() {
        var ings = ingredientService.getAll();
        if (ings.isEmpty()) return record("등록된 식자재가 없어요.");
        StringBuilder sb = new StringBuilder("식자재 " + ings.size() + "개:\n");
        for (IngredientDto i : ings) {
            sb.append("· ").append(i.getIngredientName()).append(" (").append(i.getUnit()).append(")\n");
        }
        return record(sb.toString().trim());
    }
}
