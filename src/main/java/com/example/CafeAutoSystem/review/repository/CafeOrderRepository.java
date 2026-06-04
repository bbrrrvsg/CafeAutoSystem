package com.example.CafeAutoSystem.review.repository;

import com.example.CafeAutoSystem.review.entity.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {
}
