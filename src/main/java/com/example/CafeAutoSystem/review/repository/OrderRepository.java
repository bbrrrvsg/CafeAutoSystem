package com.example.CafeAutoSystem.review.repository;

import com.example.CafeAutoSystem.review.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Menu , Long> {
}
