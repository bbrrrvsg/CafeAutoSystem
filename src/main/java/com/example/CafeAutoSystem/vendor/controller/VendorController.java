package com.example.CafeAutoSystem.vendor.controller;

import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import com.example.CafeAutoSystem.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vendor")
public class VendorController {

    private final VendorService vendorService;

    // -----------------------------------------------------
    // [GET] /api/vendor
    //   거래처 전체 목록 조회
    // -----------------------------------------------------
    @GetMapping
    public List<VendorDto> getAll() {
        return vendorService.getAll();
    }
}
