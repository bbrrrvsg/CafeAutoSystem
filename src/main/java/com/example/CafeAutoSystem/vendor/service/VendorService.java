package com.example.CafeAutoSystem.vendor.service;

import com.example.CafeAutoSystem.common.entity.VendorEntity;
import com.example.CafeAutoSystem.common.repository.VendorRepository;
import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    public List<VendorDto> getAll() {
        return vendorRepository.findAll().stream()
                .map(VendorEntity::toDto)
                .toList();
    }
}
