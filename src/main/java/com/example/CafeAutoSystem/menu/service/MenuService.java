package com.example.CafeAutoSystem.menu.service;

import com.example.CafeAutoSystem.global.outbox.OutboxService;
import com.example.CafeAutoSystem.menu.dto.MenuDto;
import com.example.CafeAutoSystem.menu.dto.MenuEventPayload;
import com.example.CafeAutoSystem.review.entity.Menu;
import com.example.CafeAutoSystem.review.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 메뉴 관리 서비스.
 *
 * 메뉴 등록/수정/삭제 시 outbox에 menu.* 이벤트를 저장한다.
 * 실제 Kafka 발행은 OutboxRelay가 담당한다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private static final String MENU_AGGREGATE_TYPE = "MENU";

    private final MenuRepository menuRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<MenuDto> getAll() {
        return menuRepository.findAll().stream()
                .map(Menu::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuDto getById(Long menuId) {
        return findOrThrow(menuId).toDto();
    }

    public MenuDto create(MenuDto dto) {
        validate(dto);

        Menu entity = Menu.builder()
                .menuName(dto.getMenuName())
                .menuPrice(dto.getMenuPrice())
                .menuImage(dto.getMenuImage())
                .build();

        Menu saved = menuRepository.save(entity);

        publishMenuEvent("menu.created", saved);

        return saved.toDto();
    }

    public MenuDto update(Long menuId, MenuDto dto) {
        Menu entity = findOrThrow(menuId);

        if (dto.getMenuName() != null) {
            entity.setMenuName(dto.getMenuName());
        }
        if (dto.getMenuPrice() != null) {
            entity.setMenuPrice(dto.getMenuPrice());
        }
        if (dto.getMenuImage() != null) {
            entity.setMenuImage(dto.getMenuImage());
        }

        publishMenuEvent("menu.updated", entity);

        return entity.toDto();
    }

    public void delete(Long menuId) {
        Menu entity = findOrThrow(menuId);

        publishMenuEvent("menu.deleted", entity);

        menuRepository.delete(entity);
    }

    private Menu findOrThrow(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. id=" + menuId));
    }

    private void validate(MenuDto dto) {
        if (dto.getMenuName() == null || dto.getMenuName().isBlank()) {
            throw new IllegalArgumentException("메뉴명(menuName)은 필수입니다.");
        }

        if (dto.getMenuPrice() == null || dto.getMenuPrice() < 0) {
            throw new IllegalArgumentException("판매가(menuPrice)는 0 이상이어야 합니다.");
        }
    }

    private void publishMenuEvent(String eventType, Menu menu) {
        MenuEventPayload payload = MenuEventPayload.builder()
                .menuId(menu.getMenuId())
                .menuName(menu.getMenuName())
                .menuPrice(menu.getMenuPrice())
                .menuImage(menu.getMenuImage())
                .build();

        JsonNode payloadNode = objectMapper.valueToTree(payload);

        outboxService.saveEvent(
                eventType,
                eventType,
                MENU_AGGREGATE_TYPE,
                String.valueOf(menu.getMenuId()),
                payloadNode
        );
    }
}