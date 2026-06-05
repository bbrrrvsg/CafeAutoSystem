package com.example.CafeAutoSystem.ingredient.controller;

import com.example.CafeAutoSystem.ingredient.dto.IngredientDto;
import com.example.CafeAutoSystem.ingredient.service.IngredientService;
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
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    // [GET] /api/ingredient — 식자재 목록
    @GetMapping
    public List<IngredientDto> getAll() {
        return ingredientService.getAll();
    }

    // [GET] /api/ingredient/{id} — 단건 조회
    @GetMapping("/{id}")
    public IngredientDto getOne(@PathVariable Integer id) {
        return ingredientService.getById(id);
    }

    // [POST] /api/ingredient — 식자재 등록
    @PostMapping
    public IngredientDto create(@RequestBody IngredientDto dto) {
        return ingredientService.create(dto);
    }

    // [PUT] /api/ingredient/{id} — 수정
    @PutMapping("/{id}")
    public IngredientDto update(@PathVariable Integer id, @RequestBody IngredientDto dto) {
        return ingredientService.update(id, dto);
    }

    // [DELETE] /api/ingredient/{id} — 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        ingredientService.delete(id);
    }
}
