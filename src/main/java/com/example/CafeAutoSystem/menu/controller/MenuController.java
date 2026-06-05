package com.example.CafeAutoSystem.menu.controller;

import com.example.CafeAutoSystem.menu.dto.MenuDto;
import com.example.CafeAutoSystem.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // [GET] /api/menu — 메뉴 목록
    @GetMapping
    public List<MenuDto> getAll() {
        return menuService.getAll();
    }

    // [GET] /api/menu/{id} — 단건 조회
    @GetMapping("/{id}")
    public MenuDto getOne(@PathVariable Long id) {
        return menuService.getById(id);
    }

    // [POST] /api/menu — 메뉴 등록 (body: menuName, menuPrice)
    @PostMapping
    public MenuDto create(@RequestBody MenuDto dto) {
        return menuService.create(dto);
    }

    // [PUT] /api/menu/{id} — 수정
    @PutMapping("/{id}")
    public MenuDto update(@PathVariable Long id, @RequestBody MenuDto dto) {
        return menuService.update(id, dto);
    }

    // [DELETE] /api/menu/{id} — 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        menuService.delete(id);
    }
}
