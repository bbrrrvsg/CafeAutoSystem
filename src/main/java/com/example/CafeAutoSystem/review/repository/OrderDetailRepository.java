package com.example.CafeAutoSystem.review.repository;

import com.example.CafeAutoSystem.review.entity.CafeOrder;
import com.example.CafeAutoSystem.review.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByCafeOrder(CafeOrder cafeOrder);
}
