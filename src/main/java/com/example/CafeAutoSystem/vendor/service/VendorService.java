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

    // 단건
    public VendorDto getById(Integer vendorId) {
        return findOrThrow(vendorId).toDto();
    }

    // 생성
    public VendorDto create(VendorDto dto) {
        validate(dto);
        VendorEntity entity = VendorEntity.builder()
                .vendorName(dto.getVendorName())
                .managerEmail(dto.getManagerEmail())
                .managerPhone(dto.getManagerPhone())
                .build();
        return vendorRepository.save(entity).toDto();
    }

    // 수정 (null 필드는 미변경)
    public VendorDto update(Integer vendorId, VendorDto dto) {
        VendorEntity entity = findOrThrow(vendorId);
        if (dto.getVendorName() != null)   entity.setVendorName(dto.getVendorName());
        if (dto.getManagerEmail() != null) entity.setManagerEmail(dto.getManagerEmail());
        if (dto.getManagerPhone() != null) entity.setManagerPhone(dto.getManagerPhone());
        return entity.toDto(); // dirty checking
    }

    // 삭제
    public void delete(Integer vendorId) {
        if (!vendorRepository.existsById(vendorId)) {
            throw new IllegalArgumentException("거래처를 찾을 수 없습니다. id=" + vendorId);
        }
        vendorRepository.deleteById(vendorId);
    }

    // ----- 내부 -----
    private VendorEntity findOrThrow(Integer vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("거래처를 찾을 수 없습니다. id=" + vendorId));
    }

    private void validate(VendorDto dto) {
        if (dto.getVendorName() == null || dto.getVendorName().isBlank()) {
            throw new IllegalArgumentException("거래처명(vendorName)은 필수입니다.");
        }
        if (dto.getManagerEmail() == null || dto.getManagerEmail().isBlank()) {
            throw new IllegalArgumentException("담당자 이메일(managerEmail)은 필수입니다.");
        }
        if (dto.getManagerPhone() == null || dto.getManagerPhone().isBlank()) {
            throw new IllegalArgumentException("담당자 전화(managerPhone)는 필수입니다.");
        }
    }
}
