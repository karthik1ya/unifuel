package com.unifuel.controller;

import com.unifuel.dto.MealPlanRequest;
import com.unifuel.dto.MealPlanResponse;
import com.unifuel.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping("/recommend")
    public MealPlanResponse recommendFoods(
           @Valid @RequestBody MealPlanRequest request) {

        return mealPlanService.recommendFoods(request);
    }
}