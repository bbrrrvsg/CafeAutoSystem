package com.example.CafeAutoSystem.order.repository;

import com.example.CafeAutoSystem.order.entity.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {
}
