package com.example.CafeAutoSystem.search.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.common.entity.VendorEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.common.repository.MenuRecipeRepository;
import com.example.CafeAutoSystem.common.repository.PurchaseOrderRepository;
import com.example.CafeAutoSystem.common.repository.VendorRepository;
import com.example.CafeAutoSystem.menu.entity.Menu;
import com.example.CafeAutoSystem.menu.repository.MenuRepository;
import com.example.CafeAutoSystem.search.dto.SearchResponse;
import com.example.CafeAutoSystem.search.dto.SearchResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final MenuRepository menuRepository;
    private final MenuRecipeRepository menuRecipeRepository;
    private final IngredientRepository ingredientRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CurrentStockLogRepository currentStockLogRepository;

    public SearchResponse search(String keyword) {
        Map<String, List<SearchResultDto>> results = new LinkedHashMap<>();

        // 레시피는 한 번만 조회해서 "식자재" + "재고이력" 두 카테고리에 재사용
        List<MenuRecipeEntity> matchedRecipes = menuRecipeRepository.searchByMenuNameWithIngredient(keyword);

        results.put("메뉴",         convertMenus(menuRepository.searchByKeyword(keyword)));
        results.put("레시피 식자재", convertRecipeIngredients(matchedRecipes));
        results.put("레시피 재고이력", convertRecipeStockLogs(matchedRecipes));
        results.put("원자재",       convertIngredients(ingredientRepository.searchByKeyword(keyword)));
        results.put("거래처",   convertVendors(vendorRepository.searchByKeyword(keyword)));
        results.put("발주이력", convertOrders(purchaseOrderRepository.searchByKeyword(keyword)));
        results.put("재고로그", convertLogs(
                currentStockLogRepository.searchByKeyword(keyword, PageRequest.of(0, 10))));

        // 결과 없는 카테고리 제거
        results.entrySet().removeIf(e -> e.getValue().isEmpty());

        int total = results.values().stream().mapToInt(List::size).sum();

        return SearchResponse.builder()
                .keyword(keyword)
                .total(total)
                .results(results)
                .build();
    }

    private List<SearchResultDto> convertMenus(List<Menu> list) {
        return list.stream().map(m -> SearchResultDto.builder()
                .category("메뉴")
                .id(m.getMenuId().intValue())
                .title(m.getMenuName())
                .subtitle(m.getMenuPrice() + "원")
                .build()).toList();
    }

    /**
     * 레시피 연결 검색:
     * 메뉴명(부분일치)에 걸린 레시피의 연결 식자재를 펼쳐서 보여준다.
     * 예) "아메리카노" 검색 → "아이스 아메리카노" 레시피 → 원두/컵 + 각 식자재 현재고.
     * 같은 식자재가 여러 레시피에 걸리면 1건으로 합친다(먼저 매칭된 메뉴 기준).
     */
    private List<SearchResultDto> convertRecipeIngredients(List<MenuRecipeEntity> recipes) {
        Map<Integer, SearchResultDto> byIngredient = new LinkedHashMap<>();
        for (MenuRecipeEntity r : recipes) {
            IngredientEntity ing = r.getIngredient();
            if (ing == null || byIngredient.containsKey(ing.getIngredientId())) continue;

            int stock = currentStockLogRepository.convertToCurrentStock(ing.getIngredientId());
            byIngredient.put(ing.getIngredientId(), SearchResultDto.builder()
                    .category("레시피 식자재")
                    .id(ing.getIngredientId())
                    .title(ing.getIngredientName())
                    .subtitle(String.format("%s 레시피 · 1잔당 %d%s · 현재고 %d%s",
                            r.getMenuName(), r.getRequiredQuantity(), ing.getUnit(), stock, ing.getUnit()))
                    .build());
        }
        return new ArrayList<>(byIngredient.values());
    }

    /**
     * 레시피 연결 검색 - 재고이력:
     * 매칭된 레시피의 연결 식자재들의 최근 재고로그를 펼쳐서 보여준다.
     * 예) "아메리카노" 검색 → 원두/컵의 입고·차감·폐기 로그 최신순.
     */
    private List<SearchResultDto> convertRecipeStockLogs(List<MenuRecipeEntity> recipes) {
        List<Integer> ingredientIds = recipes.stream()
                .map(MenuRecipeEntity::getIngredient)
                .filter(i -> i != null)
                .map(IngredientEntity::getIngredientId)
                .distinct()
                .toList();

        if (ingredientIds.isEmpty()) return List.of();

        List<CurrentStockLogEntity> logs = currentStockLogRepository
                .findRecentByIngredientIds(ingredientIds, PageRequest.of(0, 10));

        return logs.stream().map(c -> SearchResultDto.builder()
                .category("레시피 재고이력")
                .id(c.getLogId() != null ? c.getLogId().intValue() : null)
                .title(c.getMessage())
                .subtitle(c.getLogType())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .build()).toList();
    }

    private List<SearchResultDto> convertIngredients(List<IngredientEntity> list) {
        return list.stream().map(i -> SearchResultDto.builder()
                .category("원자재")
                .id(i.getIngredientId())
                .title(i.getIngredientName())
                .subtitle("단위: " + i.getUnit() + " / 안전재고: " + i.getSafetyStock() + i.getUnit())
                .build()).toList();
    }

    private List<SearchResultDto> convertVendors(List<VendorEntity> list) {
        return list.stream().map(v -> SearchResultDto.builder()
                .category("거래처")
                .id(v.getVendorId())
                .title(v.getVendorName())
                .subtitle(v.getManagerEmail())
                .build()).toList();
    }

    private List<SearchResultDto> convertOrders(List<PurchaseOrderEntity> list) {
        return list.stream().map(p -> {
            String ingredientName = p.getVendorIngredient() != null &&
                    p.getVendorIngredient().getIngredient() != null
                    ? p.getVendorIngredient().getIngredient().getIngredientName() : "-";
            String vendorName = p.getVendorIngredient() != null &&
                    p.getVendorIngredient().getVendor() != null
                    ? p.getVendorIngredient().getVendor().getVendorName() : "-";
            return SearchResultDto.builder()
                    .category("발주이력")
                    .id(p.getOrderItemId())
                    .title(ingredientName + " " + p.getFinalQty() + " 발주")
                    .subtitle(vendorName)
                    .status(p.getStatus())
                    .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : null)
                    .build();
        }).toList();
    }

    private List<SearchResultDto> convertLogs(List<CurrentStockLogEntity> list) {
        return list.stream().map(c -> SearchResultDto.builder()
                .category("재고로그")
                .id(c.getLogId() != null ? c.getLogId().intValue() : null)
                .title(c.getMessage())
                .subtitle(c.getLogType())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .build()).toList();
    }
}
