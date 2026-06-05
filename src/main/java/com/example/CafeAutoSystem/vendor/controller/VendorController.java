package com.example.CafeAutoSystem.vendor.controller;

import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import com.example.CafeAutoSystem.vendor.service.VendorService;
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

    // [GET] /api/vendor/{id} — 단건 조회
    @GetMapping("/{id}")
    public VendorDto getOne(@PathVariable Integer id) {
        return vendorService.getById(id);
    }

    // [POST] /api/vendor — 거래처 등록 (body: vendorName, managerEmail, managerPhone)
    @PostMapping
    public VendorDto create(@RequestBody VendorDto dto) {
        return vendorService.create(dto);
    }

    // [PUT] /api/vendor/{id} — 수정
    @PutMapping("/{id}")
    public VendorDto update(@PathVariable Integer id, @RequestBody VendorDto dto) {
        return vendorService.update(id, dto);
    }

    // [DELETE] /api/vendor/{id} — 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        vendorService.delete(id);
    }
}
