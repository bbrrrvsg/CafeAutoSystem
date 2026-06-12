package com.example.CafeAutoSystem.menu.repository;

import com.example.CafeAutoSystem.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findFirstByMenuNameOrderByMenuIdAsc(String menuName);
}