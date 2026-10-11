package com.unifuel.controller;

import com.unifuel.dto.NutritionRequest;
import com.unifuel.dto.NutritionResponse;
import com.unifuel.service.NutritionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @PostMapping("/calculate")
    public NutritionResponse calculate(
            @RequestBody NutritionRequest request) {

        return nutritionService.calculate(request);
    }
}