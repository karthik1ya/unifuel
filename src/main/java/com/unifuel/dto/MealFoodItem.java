package com.unifuel.dto;

public class MealFoodItem {

    private String foodName;
    private double quantityGrams;
    private double calories;
    private double protein;
    private double cost;

    public MealFoodItem() {
    }

    public MealFoodItem(
            String foodName,
            double quantityGrams,
            double calories,
            double protein,
            double cost) {

        this.foodName = foodName;
        this.quantityGrams = quantityGrams;
        this.calories = calories;
        this.protein = protein;
        this.cost = cost;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getQuantityGrams() {
        return quantityGrams;
    }

    public void setQuantityGrams(double quantityGrams) {
        this.quantityGrams = quantityGrams;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}