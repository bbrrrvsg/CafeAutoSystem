package com.example.CafeAutoSystem.search.service;

import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.entity.PurchaseOrderEntity;
import com.example.CafeAutoSystem.common.entity.VendorEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final MenuRepository menuRepository;
    private final IngredientRepository ingredientRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CurrentStockLogRepository currentStockLogRepository;

    public SearchResponse search(String keyword) {
        Map<String, List<SearchResultDto>> results = new LinkedHashMap<>();

        results.put("메뉴",     convertMenus(menuRepository.searchByKeyword(keyword)));
        results.put("원자재",   convertIngredients(ingredientRepository.searchByKeyword(keyword)));
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
