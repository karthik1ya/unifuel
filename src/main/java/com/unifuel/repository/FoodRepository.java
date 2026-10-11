package com.unifuel.repository;

import com.unifuel.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByDietTypeIgnoreCase(String dietType);

    List<Food> findByNameContainingIgnoreCase(String name);
}