package com.example.CafeAutoSystem.vendor.entity;

import com.example.CafeAutoSystem.common.entity.BaseTime;
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
public class Vendor extends BaseTime {

    // 거래처번호 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Integer vendorId;

    // 거래처명
    @Column(name = "vendor_name", length = 100, nullable = false)
    private String vendorName;

    // 담당자 이메일 (unique)
    @Column(name = "manager_email", length = 100, nullable = false, unique = true)
    private String managerEmail;

    // 담당자 연락처
    @Column(name = "manager_phone", length = 20, nullable = false)
    private String managerPhone;

    // createDate / updateDate 는 BaseTime 상속

    // -----------------------------------------------------
    // 엔티티 → DTO 변환
    // -----------------------------------------------------
    public VendorDto toDto() {
        return VendorDto.builder()
                .vendorId(this.vendorId)
                .vendorName(this.vendorName)
                .managerEmail(this.managerEmail)
                .managerPhone(this.managerPhone)
                .createDate(getCreateDate())   // BaseTime
                .updateDate(getUpdateDate())   // BaseTime
                .build();
    }
}
