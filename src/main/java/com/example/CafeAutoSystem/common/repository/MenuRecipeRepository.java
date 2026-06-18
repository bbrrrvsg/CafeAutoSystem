package com.example.CafeAutoSystem.common.repository;

import com.example.CafeAutoSystem.common.entity.MenuRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MenuRecipeRepository extends JpaRepository<MenuRecipeEntity, Long> {

    @Query("SELECT r FROM MenuRecipeEntity r JOIN FETCH r.ingredient WHERE r.menuName = :menuName")
    List<MenuRecipeEntity> findByMenuNameWithIngredient(@Param("menuName") String menuName);

    /** 통합 검색용 - 메뉴명 부분일치로 레시피 조회 (연결된 식자재 fetch) */
    @Query("SELECT r FROM MenuRecipeEntity r JOIN FETCH r.ingredient WHERE r.menuName LIKE %:keyword%")
    List<MenuRecipeEntity> searchByMenuNameWithIngredient(@Param("keyword") String keyword);
}