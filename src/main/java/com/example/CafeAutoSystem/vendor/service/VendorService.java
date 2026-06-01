package com.example.CafeAutoSystem.vendor.service;

import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import com.example.CafeAutoSystem.vendor.entity.Vendor;
import com.example.CafeAutoSystem.vendor.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    // -----------------------------------------------------
    // 거래처 전체 목록 (수정 폼 드롭다운 등에서 사용)
    // -----------------------------------------------------
    public List<VendorDto> getAll() {
        return vendorRepository.findAll().stream()
                .map(Vendor::toDto)
                .toList();
    }
}
