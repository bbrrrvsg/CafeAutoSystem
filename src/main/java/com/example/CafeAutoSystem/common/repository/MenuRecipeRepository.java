package com.example.CafeAutoSystem.jms_ai_rpa.repository;

import com.example.CafeAutoSystem.jms_ai_rpa.entity.MenuRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MenuRecipeRepository extends JpaRepository<MenuRecipeEntity, Long> {

    @Query("SELECT r FROM MenuRecipeEntity r JOIN FETCH r.ingredient WHERE r.menuName = :menuName")
    List<MenuRecipeEntity> findByMenuNameWithIngredient(@Param("menuName") String menuName);
}