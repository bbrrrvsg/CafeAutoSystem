package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Integer> {

    @Query("SELECT v FROM VendorEntity v WHERE v.vendorName LIKE %:keyword% OR v.managerEmail LIKE %:keyword%")
    List<VendorEntity> searchByKeyword(@Param("keyword") String keyword);
}
