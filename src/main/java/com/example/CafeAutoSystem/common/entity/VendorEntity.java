package com.example.CafeAutoSystem.common.entity;

import com.example.CafeAutoSystem.vendor.dto.VendorDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendor")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VendorEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Integer vendorId;

    @Column(name = "vendor_name", length = 100, nullable = false)
    private String vendorName;

    @Column(name = "manager_email", length = 100, nullable = false, unique = true)
    private String managerEmail;

    @Column(name = "manager_phone", length = 20, nullable = false)
    private String managerPhone;

    public VendorDto toDto() {
        return VendorDto.builder()
                .vendorId(this.vendorId)
                .vendorName(this.vendorName)
                .managerEmail(this.managerEmail)
                .managerPhone(this.managerPhone)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
