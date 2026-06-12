package com.example.CafeAutoSystem.menu.service;

import com.example.CafeAutoSystem.global.outbox.OutboxService;
import com.example.CafeAutoSystem.menu.dto.MenuDto;
import com.example.CafeAutoSystem.menu.dto.MenuEventPayload;
import com.example.CafeAutoSystem.menu.entity.Menu;
import com.example.CafeAutoSystem.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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

    /**
     * 메뉴 직접 등록 API.
     * 같은 메뉴명이 이미 있으면 중복 INSERT하지 않고 가격/이미지만 갱신한다.
     */
    public MenuDto create(MenuDto dto) {
        validate(dto);

        Menu saved = upsertMenuEntity(
                dto.getMenuName(),
                dto.getMenuPrice(),
                dto.getMenuImage()
        );

        return saved.toDto();
    }

    /**
     * 레시피 등록/수정 시 호출되는 메뉴 동기화 메서드.
     *
     * 핵심:
     * MENU_RECIPE에만 저장하면 구매 서버 POS에 메뉴가 안 뜰 수 있다.
     * 그래서 레시피 등록 시에도 반드시 menu 테이블을 생성/갱신하고,
     * menu.created 또는 menu.updated 이벤트를 발행한다.
     */
    public MenuDto upsertFromRecipe(String menuName, Integer menuPrice) {
        validateMenuNameAndPrice(menuName, menuPrice);

        Menu saved = upsertMenuEntity(menuName, menuPrice, null);

        return saved.toDto();
    }

    public MenuDto update(Long menuId, MenuDto dto) {
        Menu entity = findOrThrow(menuId);

        if (dto.getMenuName() != null && !dto.getMenuName().isBlank()) {
            entity.setMenuName(dto.getMenuName());
        }

        if (dto.getMenuPrice() != null) {
            if (dto.getMenuPrice() < 0) {
                throw new IllegalArgumentException("판매가(menuPrice)는 0 이상이어야 합니다.");
            }
            entity.setMenuPrice(dto.getMenuPrice());
        }

        if (dto.getMenuImage() != null) {
            entity.setMenuImage(dto.getMenuImage());
        }

        Menu saved = menuRepository.save(entity);

        publishMenuEvent("menu.updated", saved);

        return saved.toDto();
    }

    public void delete(Long menuId) {
        Menu entity = findOrThrow(menuId);

        publishMenuEvent("menu.deleted", entity);

        menuRepository.delete(entity);
    }

    private Menu upsertMenuEntity(String menuName, Integer menuPrice, String menuImage) {
        validateMenuNameAndPrice(menuName, menuPrice);

        return menuRepository.findFirstByMenuNameOrderByMenuIdAsc(menuName)
                .map(existing -> {
                    existing.setMenuPrice(menuPrice);

                    if (menuImage != null) {
                        existing.setMenuImage(menuImage);
                    }

                    Menu saved = menuRepository.save(existing);
                    publishMenuEvent("menu.updated", saved);
                    return saved;
                })
                .orElseGet(() -> {
                    Menu entity = Menu.builder()
                            .menuName(menuName)
                            .menuPrice(menuPrice)
                            .menuImage(menuImage)
                            .build();

                    Menu saved = menuRepository.save(entity);
                    publishMenuEvent("menu.created", saved);
                    return saved;
                });
    }

    private Menu findOrThrow(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. id=" + menuId));
    }

    private void validate(MenuDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("메뉴 요청 데이터가 비어 있습니다.");
        }

        validateMenuNameAndPrice(dto.getMenuName(), dto.getMenuPrice());
    }

    private void validateMenuNameAndPrice(String menuName, Integer menuPrice) {
        if (menuName == null || menuName.isBlank()) {
            throw new IllegalArgumentException("메뉴명(menuName)은 필수입니다.");
        }

        if (menuPrice == null || menuPrice < 0) {
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