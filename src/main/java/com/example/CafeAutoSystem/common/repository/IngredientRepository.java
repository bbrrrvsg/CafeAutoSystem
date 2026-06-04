package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<IngredientEntity,Integer> {
    /** 비관적 락 - 동시성 제어용 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM IngredientEntity i WHERE i.ingredientId = :id")
    Optional<IngredientEntity> findByIdForUpdate(@Param("id") Integer id);
}
