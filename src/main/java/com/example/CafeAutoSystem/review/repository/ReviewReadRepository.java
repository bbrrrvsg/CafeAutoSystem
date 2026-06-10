package com.example.CafeAutoSystem.review.repository;

import com.example.CafeAutoSystem.review.entity.ReviewRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReadRepository extends JpaRepository<ReviewRead, Long> {

    Page<ReviewRead> findAllByOrderByReviewIdDesc(Pageable pageable);

}