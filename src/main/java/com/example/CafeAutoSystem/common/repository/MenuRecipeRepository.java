package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRecipeRepository extends JpaRepository<MenuRecipeEntity, Long> {

    @EntityGraph(attributePaths = {"ingredient"})
    List<MenuRecipeEntity> findByMenuName(String menuName);
}