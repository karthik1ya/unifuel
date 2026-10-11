package com.unifuel.dto;

public class NutritionResponse {

    private double bmr;
    private double tdee;
    private double targetCalories;
    private double targetProtein;

    public NutritionResponse(
            double bmr,
            double tdee,
            double targetCalories,
            double targetProtein) {

        this.bmr = bmr;
        this.tdee = tdee;
        this.targetCalories = targetCalories;
        this.targetProtein = targetProtein;
    }

    public double getBmr() {
        return bmr;
    }

    public double getTdee() {
        return tdee;
    }

    public double getTargetCalories() {
        return targetCalories;
    }

    public double getTargetProtein() {
        return targetProtein;
    }
}