package com.example.CafeAutoSystem.vendor.dto;

import com.example.CafeAutoSystem.vendor.entity.Vendor;
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

    // 거래처번호 (PK)
    private Integer vendorId;

    // 거래처명
    private String vendorName;

    // 담당자 이메일
    private String managerEmail;

    // 담당자 연락처
    private String managerPhone;

    // 등록일 (BaseTime)
    private LocalDateTime createDate;

    // 수정일 (BaseTime)
    private LocalDateTime updateDate;

    // -----------------------------------------------------
    // DTO → 엔티티 변환
    //   createDate / updateDate 은 JPA Auditing 이 자동 주입하므로 제외
    // -----------------------------------------------------
    public Vendor toEntity() {
        return Vendor.builder()
                .vendorId(this.vendorId)
                .vendorName(this.vendorName)
                .managerEmail(this.managerEmail)
                .managerPhone(this.managerPhone)
                .build();
    }
}
