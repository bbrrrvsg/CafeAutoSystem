package com.example.CafeAutoSystem.menurecipe.controller;

import com.example.CafeAutoSystem.menurecipe.dto.MenuRecipeDto;
import com.example.CafeAutoSystem.menurecipe.service.MenuRecipeService;
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
@RequestMapping("/api/menu-recipe")
@RequiredArgsConstructor
public class MenuRecipeController {

    private final MenuRecipeService menuRecipeService;

    // [GET] /api/menu-recipe — 레시피 목록
    @GetMapping
    public List<MenuRecipeDto> getAll() {
        return menuRecipeService.getAll();
    }

    // [GET] /api/menu-recipe/{id} — 단건 조회
    @GetMapping("/{id}")
    public MenuRecipeDto getOne(@PathVariable Long id) {
        return menuRecipeService.getById(id);
    }

    // [GET] /api/menu-recipe/by-menu/{menuName} — 메뉴명으로 레시피 조회
    @GetMapping("/by-menu/{menuName}")
    public List<MenuRecipeDto> getByMenu(@PathVariable String menuName) {
        return menuRecipeService.getByMenuName(menuName);
    }

    // [POST] /api/menu-recipe — 레시피 등록 (body: menuName, price, ingredientId, requiredQuantity, note?)
    @PostMapping
    public MenuRecipeDto create(@RequestBody MenuRecipeDto dto) {
        return menuRecipeService.create(dto);
    }

    // [PUT] /api/menu-recipe/{id} — 수정
    @PutMapping("/{id}")
    public MenuRecipeDto update(@PathVariable Long id, @RequestBody MenuRecipeDto dto) {
        return menuRecipeService.update(id, dto);
    }

    // [DELETE] /api/menu-recipe/{id} — 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        menuRecipeService.delete(id);
    }
}
