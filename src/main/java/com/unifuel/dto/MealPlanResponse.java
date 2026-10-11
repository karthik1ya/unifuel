package com.unifuel.dto;

import java.util.ArrayList;
import java.util.List;

public class MealPlanResponse {

    private List<MealSection> meals = new ArrayList<>();

    private double totalCost;
    private double totalProtein;
    private double totalCalories;

    private double targetCalories;
    private double targetProtein;

    private boolean calorieTargetMet;
    private boolean proteinTargetMet;
    private boolean budgetMet;

    public MealPlanResponse() {
    }

    public List<MealSection> getMeals() {
        return meals;
    }

    public void setMeals(List<MealSection> meals) {
        this.meals = meals;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(double targetCalories) {
        this.targetCalories = targetCalories;
    }

    public double getTargetProtein() {
        return targetProtein;
    }

    public void setTargetProtein(double targetProtein) {
        this.targetProtein = targetProtein;
    }

    public boolean isCalorieTargetMet() {
        return calorieTargetMet;
    }

    public void setCalorieTargetMet(boolean calorieTargetMet) {
        this.calorieTargetMet = calorieTargetMet;
    }

    public boolean isProteinTargetMet() {
        return proteinTargetMet;
    }

    public void setProteinTargetMet(boolean proteinTargetMet) {
        this.proteinTargetMet = proteinTargetMet;
    }

    public boolean isBudgetMet() {
        return budgetMet;
    }

    public void setBudgetMet(boolean budgetMet) {
        this.budgetMet = budgetMet;
    }
}