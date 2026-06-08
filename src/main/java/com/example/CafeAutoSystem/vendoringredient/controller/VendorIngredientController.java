package com.example.CafeAutoSystem.vendoringredient.controller;

import com.example.CafeAutoSystem.vendoringredient.dto.VendorIngredientDto;
import com.example.CafeAutoSystem.vendoringredient.service.VendorIngredientService;
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
@RequiredArgsConstructor
@RequestMapping("/api/vendor-ingredient")
public class VendorIngredientController {

    private final VendorIngredientService vendorIngredientService;

    // [GET] /api/vendor-ingredient — 전체 매핑 목록
    @GetMapping
    public List<VendorIngredientDto> getAll() {
        return vendorIngredientService.getAll();
    }

    // [GET] /api/vendor-ingredient/{id} — 단건 조회
    @GetMapping("/{id}")
    public VendorIngredientDto getOne(@PathVariable Integer id) {
        return vendorIngredientService.getById(id);
    }

    // [GET] /api/vendor-ingredient/by-ingredient/{ingredientId} — 특정 식자재의 거래처 1/2/3순위 목록
    @GetMapping("/by-ingredient/{ingredientId}")
    public List<VendorIngredientDto> getByIngredient(@PathVariable Integer ingredientId) {
        return vendorIngredientService.getByIngredient(ingredientId);
    }

    // [POST] /api/vendor-ingredient — 매핑 등록 (body: vendorId, ingredientId, unitPrice)
    //   등록 후 단가순으로 priority_rank 자동 재계산
    @PostMapping
    public VendorIngredientDto create(@RequestBody VendorIngredientDto dto) {
        return vendorIngredientService.create(dto);
    }

    // [PUT] /api/vendor-ingredient/{id} — 수정 (단가 변경 시 재계산)
    @PutMapping("/{id}")
    public VendorIngredientDto update(@PathVariable Integer id, @RequestBody VendorIngredientDto dto) {
        return vendorIngredientService.update(id, dto);
    }

    // [DELETE] /api/vendor-ingredient/{id} — 삭제 (삭제 후 순위 재정렬)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        vendorIngredientService.delete(id);
    }
}
