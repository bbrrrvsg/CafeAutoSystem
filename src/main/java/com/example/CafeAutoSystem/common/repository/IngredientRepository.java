package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<IngredientEntity,Long> {
}
