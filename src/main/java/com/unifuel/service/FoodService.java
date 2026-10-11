package com.unifuel.service;

import com.unifuel.entity.Food;
import com.unifuel.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public Food addFood(Food food) {
        return foodRepository.save(food);
    }

    public List<Food> getFoodsByDietType(String dietType) {
        return foodRepository.findByDietTypeIgnoreCase(dietType);
    }

    public List<Food> searchFoods(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name);
    }

    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }
}