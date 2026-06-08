package com.example.CafeAutoSystem.menu.kafka;

import com.example.CafeAutoSystem.menu.dto.MenuDto;
import com.example.CafeAutoSystem.menu.dto.MenuItemEvent;
import com.example.CafeAutoSystem.menu.dto.MenuListQueryRequestEvent;
import com.example.CafeAutoSystem.menu.dto.MenuListQueryResultEvent;
import com.example.CafeAutoSystem.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 구매 서버가 보낸 메뉴 목록 조회 요청을 Kafka로 수신하고,
 * 사장 서버 DB의 메뉴 목록을 다시 Kafka로 돌려준다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuListQueryRequestConsumer {

    private final ObjectMapper objectMapper;
    private final MenuService menuService;
    private final MenuListQueryResultProducer resultProducer;

    @KafkaListener(
            topics = "menu-list-query-request",
            groupId = "owner-menu-list-service"
    )
    public void consume(String message) {
        MenuListQueryRequestEvent request = null;

        try {
            log.info("📩 메뉴 목록 조회 요청 수신: {}", message);

            request = objectMapper.readValue(message, MenuListQueryRequestEvent.class);

            List<MenuDto> menuDtos = menuService.getAll();

            List<MenuItemEvent> menus = menuDtos.stream()
                    .map(menu -> MenuItemEvent.builder()
                            .menuId(menu.getMenuId())
                            .menuName(menu.getMenuName())
                            .menuPrice(menu.getMenuPrice())
                            .menuImage(menu.getMenuImage())
                            .build()
                    )
                    .toList();

            MenuListQueryResultEvent result = MenuListQueryResultEvent.builder()
                    .requestId(request.getRequestId())
                    .success(true)
                    .message("메뉴 목록 조회 성공")
                    .menus(menus)
                    .build();

            resultProducer.send(result);

        } catch (Exception e) {
            log.error("메뉴 목록 조회 요청 처리 실패", e);

            if (request != null && request.getRequestId() != null) {
                MenuListQueryResultEvent failResult = MenuListQueryResultEvent.builder()
                        .requestId(request.getRequestId())
                        .success(false)
                        .message("메뉴 목록 조회 실패: " + e.getMessage())
                        .menus(List.of())
                        .build();

                resultProducer.send(failResult);
            }
        }
    }
}