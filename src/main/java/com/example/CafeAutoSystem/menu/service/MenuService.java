package com.example.CafeAutoSystem.menu.service;

import com.example.CafeAutoSystem.menu.dto.MenuDto;
import com.example.CafeAutoSystem.review.repository.MenuRepository;
import com.example.CafeAutoSystem.review.entity.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    // 목록
    @Transactional(readOnly = true)
    public List<MenuDto> getAll() {
        return menuRepository.findAll().stream()
                .map(Menu::toDto)
                .toList();
    }

    // 단건
    @Transactional(readOnly = true)
    public MenuDto getById(Long menuId) {
        return findOrThrow(menuId).toDto();
    }

    // 생성
    public MenuDto create(MenuDto dto) {
        validate(dto);
        Menu entity = Menu.builder()
                .menuName(dto.getMenuName())
                .menuPrice(dto.getMenuPrice())
                .menuImage(dto.getMenuImage())
                .build();
        return menuRepository.save(entity).toDto();
    }

    // 수정 (null 필드는 미변경)
    public MenuDto update(Long menuId, MenuDto dto) {
        Menu entity = findOrThrow(menuId);
        if (dto.getMenuName() != null)  entity.setMenuName(dto.getMenuName());
        if (dto.getMenuPrice() != null) entity.setMenuPrice(dto.getMenuPrice());
        if (dto.getMenuImage() != null) entity.setMenuImage(dto.getMenuImage());
        return entity.toDto(); // dirty checking
    }

    // 삭제
    public void delete(Long menuId) {
        if (!menuRepository.existsById(menuId)) {
            throw new IllegalArgumentException("메뉴를 찾을 수 없습니다. id=" + menuId);
        }
        menuRepository.deleteById(menuId);
    }

    // ----- 내부 -----
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
}
