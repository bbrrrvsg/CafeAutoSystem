package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.dto.InventoryResponse;
import com.example.CafeAutoSystem.ai_rpa.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * [GET] 전체 재고 현황 조회
     * → inventory.jsp 에서 fetch 호출
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventoryList() {
        return ResponseEntity.ok(inventoryService.getInventoryList());
    }
}
