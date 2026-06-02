package com.example.CafeAutoSystem.review.repository;

import com.example.CafeAutoSystem.review.entity.CafeOrder;
import com.example.CafeAutoSystem.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByCafeOrder(CafeOrder cafeOrder);
}
