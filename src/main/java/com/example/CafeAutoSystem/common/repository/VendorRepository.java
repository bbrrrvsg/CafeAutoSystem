package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Integer> {
}
