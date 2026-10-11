package com.unifuel.dto;

import java.util.ArrayList;
import java.util.List;

public class MealSection {

    private String name;
    private List<MealFoodItem> foods = new ArrayList<>();
    private double totalCalories;
    private double totalProtein;
    private double totalCost;

    public MealSection() {
    }

    public MealSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MealFoodItem> getFoods() {
        return foods;
    }

    public void setFoods(List<MealFoodItem> foods) {
        this.foods = foods;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}