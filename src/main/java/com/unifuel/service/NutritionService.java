package com.unifuel.service;

import com.unifuel.dto.NutritionRequest;
import com.unifuel.dto.NutritionResponse;
import org.springframework.stereotype.Service;

@Service
public class NutritionService {

    public NutritionResponse calculate(NutritionRequest request) {

        double weight = request.getWeight();
        double height = request.getHeight();
        int age = request.getAge();

        double bmr;

        if ("MALE".equalsIgnoreCase(request.getGender())) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else if ("FEMALE".equalsIgnoreCase(request.getGender())) {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        } else {
            throw new IllegalArgumentException(
                    "Gender must be MALE or FEMALE");
        }

        double activityMultiplier;

        switch (request.getActivityLevel().toUpperCase()) {
            case "SEDENTARY":
                activityMultiplier = 1.2;
                break;
            case "LIGHT":
                activityMultiplier = 1.375;
                break;
            case "MODERATE":
                activityMultiplier = 1.55;
                break;
            case "VERY_ACTIVE":
                activityMultiplier = 1.725;
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid activity level");
        }

        double tdee = bmr * activityMultiplier;

        double targetCalories;
        double proteinMultiplier;

        switch (request.getFitnessGoal().toUpperCase()) {
            case "MUSCLE_GAIN":
                targetCalories = tdee + 250;
                proteinMultiplier = 1.8;
                break;

            case "FAT_LOSS":
                targetCalories = tdee - 300;
                proteinMultiplier = 1.6;
                break;

            case "MAINTENANCE":
                targetCalories = tdee;
                proteinMultiplier = 1.4;
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid fitness goal");
        }

        double targetProtein = weight * proteinMultiplier;

        return new NutritionResponse(
                Math.round(bmr),
                Math.round(tdee),
                Math.round(targetCalories),
                Math.round(targetProtein)
        );
    }
}