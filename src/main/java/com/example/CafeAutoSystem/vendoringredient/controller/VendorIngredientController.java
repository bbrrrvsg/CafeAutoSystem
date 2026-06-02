package com.example.CafeAutoSystem.vendoringredient.controller;

import com.example.CafeAutoSystem.vendoringredient.dto.VendorIngredientDto;
import com.example.CafeAutoSystem.vendoringredient.service.VendorIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vendor-ingredient")
public class VendorIngredientController {

    private final VendorIngredientService vendorIngredientService;

    // 특정 식자재의 거래처 1/2/3순위 목록
    @GetMapping("/by-ingredient/{ingredientId}")
    public List<VendorIngredientDto> getByIngredient(@PathVariable Integer ingredientId) {
        return vendorIngredientService.getByIngredient(ingredientId);
    }
}
