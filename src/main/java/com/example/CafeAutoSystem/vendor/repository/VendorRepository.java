package com.example.CafeAutoSystem.vendor.repository;

import com.example.CafeAutoSystem.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    // JpaRepository 기본 제공: save / findById / findAll / deleteById / count ...

}
