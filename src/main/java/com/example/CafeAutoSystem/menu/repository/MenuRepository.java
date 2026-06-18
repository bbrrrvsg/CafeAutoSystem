package com.example.CafeAutoSystem.menu.repository;

import com.example.CafeAutoSystem.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findFirstByMenuNameOrderByMenuIdAsc(String menuName);

    @Query("SELECT m FROM Menu m WHERE m.menuName LIKE %:keyword%")
    List<Menu> searchByKeyword(@Param("keyword") String keyword);
}