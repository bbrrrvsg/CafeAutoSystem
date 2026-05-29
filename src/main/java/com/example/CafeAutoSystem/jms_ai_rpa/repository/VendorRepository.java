package com.example.CafeAutoSystem.jms_ai_rpa.repository;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Integer> {
    // RPA 메일 발송 시, 거래처 이메일을 기반으로 단건 조회할 때 사용
    Optional<VendorEntity> findByManagerEmail(String managerEmail);
}
