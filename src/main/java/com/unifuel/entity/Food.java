package com.unifuel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double caloriesPer100g;

    private double proteinPer100g;

    private double carbohydratesPer100g;

    private double fatPer100g;

    private double pricePer100g;

    private String dietType;

    public Food() {
    }

    public Food(String name, double caloriesPer100g,
                double proteinPer100g,
                double carbohydratesPer100g,
                double fatPer100g,
                double pricePer100g,
                String dietType) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbohydratesPer100g = carbohydratesPer100g;
        this.fatPer100g = fatPer100g;
        this.pricePer100g = pricePer100g;
        this.dietType = dietType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public double getCarbohydratesPer100g() {
        return carbohydratesPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public double getPricePer100g() {
        return pricePer100g;
    }

    public String getDietType() {
        return dietType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public void setCarbohydratesPer100g(double carbohydratesPer100g) {
        this.carbohydratesPer100g = carbohydratesPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public void setPricePer100g(double pricePer100g) {
        this.pricePer100g = pricePer100g;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }
}