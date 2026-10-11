package com.unifuel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MealPlanRequest {

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Budget must be greater than zero")
    private double budget;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Daily calories must be greater than zero")
    private double dailyCalories = 2500;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Daily protein must be greater than zero")
    private double dailyProtein = 120;

    @NotBlank(message = "Diet type is required")
    @Pattern(
            regexp = "(?i)VEGETARIAN|EGGETARIAN|NON_VEGETARIAN",
            message = "Invalid diet type"
    )
    private String dietType;

    @NotBlank(message = "Fitness goal is required")
    @Pattern(
            regexp = "(?i)MUSCLE_GAIN|FAT_LOSS|MAINTENANCE",
            message = "Invalid fitness goal"
    )
    private String fitnessGoal;

    public MealPlanRequest() {
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getDailyCalories() {
        return dailyCalories;
    }

    public void setDailyCalories(double dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public double getDailyProtein() {
        return dailyProtein;
    }

    public void setDailyProtein(double dailyProtein) {
        this.dailyProtein = dailyProtein;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }
}