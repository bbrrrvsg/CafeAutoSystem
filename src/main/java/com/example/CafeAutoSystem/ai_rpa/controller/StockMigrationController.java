package com.example.CafeAutoSystem.ai_rpa.controller;

import com.example.CafeAutoSystem.ai_rpa.service.StockMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 발표 데모용: 장부 마감을 수동으로 트리거.
// 운영에서는 @Scheduled 가 정해진 시각에 자동 실행하므로 이 엔드포인트는 임시.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class StockMigrationController {

    private final StockMigrationService stockMigrationService;

    @PostMapping("/migrate")
    public Map<String, Object> migrate() {
        stockMigrationService.backup();
        return Map.of("status", "ok", "message", "장부 마감 실행 완료");
    }
}
