package com.example.CafeAutoSystem.vendor.dto;

import com.example.CafeAutoSystem.common.entity.Vendor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VendorDto {

    private Integer vendorId;
    private String vendorName;
    private String managerEmail;
    private String managerPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vendor toEntity() {
        return Vendor.builder()
                .vendorId(this.vendorId)
                .vendorName(this.vendorName)
                .managerEmail(this.managerEmail)
                .managerPhone(this.managerPhone)
                .build();
    }
}
