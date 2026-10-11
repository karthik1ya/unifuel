package com.unifuel.controller;

import com.unifuel.entity.Food;
import com.unifuel.service.FoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        Food savedFood = foodService.addFood(food);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFood);
    }

    @GetMapping("/diet/{dietType}")
    public List<Food> getFoodsByDietType(@PathVariable String dietType) {
        return foodService.getFoodsByDietType(dietType);
    }

    @GetMapping("/search")
    public List<Food> searchFoods(@RequestParam String name) {
        return foodService.searchFoods(name);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}