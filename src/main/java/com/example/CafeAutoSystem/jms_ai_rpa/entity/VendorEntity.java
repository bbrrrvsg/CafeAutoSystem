package com.example.CafeAutoSystem.jms_ai_rpa.entity;

import com.example.CafeAutoSystem.global.config.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "VENDOR")
public class VendorEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Integer vendorId; // 거래처번호 (PK)

    @Column(name = "vendor_name", nullable = false, length = 100)
    private String vendorName; // 거래처명

    @Column(name = "manager_email", nullable = false, length = 100, unique = true)
    private String managerEmail; // 담당자이메일 (RPA 발송용)

    @Column(name = "manager_phone", length = 20)
    private String managerPhone; // 담당자연락처
}
