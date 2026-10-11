package com.unifuel.service;

import com.unifuel.dto.MealFoodItem;
import com.unifuel.dto.MealPlanRequest;
import com.unifuel.dto.MealPlanResponse;
import com.unifuel.dto.MealSection;
import com.unifuel.entity.Food;
import com.unifuel.repository.FoodRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class MealPlanService {

    private final FoodRepository foodRepository;

    public MealPlanService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public MealPlanResponse recommendFoods(MealPlanRequest request) {

        validateRequest(request);

        List<Food> foods = foodRepository.findAll()
                .stream()
                .filter(food -> isAllowed(food, request.getDietType()))
                .filter(food -> food.getPricePer100g() > 0)
                .filter(food -> food.getCaloriesPer100g() > 0)
                .filter(food -> food.getProteinPer100g() >= 0)
                .sorted(Comparator.comparingDouble(this::scoreFood).reversed())
                .toList();

        if (foods.isEmpty()) {
            throw new IllegalArgumentException(
                    "No suitable foods are available for this diet."
            );
        }

        List<MealSection> meals = new ArrayList<>();
        meals.add(new MealSection("Breakfast"));
        meals.add(new MealSection("Lunch"));
        meals.add(new MealSection("Pre-workout"));
        meals.add(new MealSection("Dinner"));

        double[] calorieShares = {0.25, 0.35, 0.10, 0.30};
        double[] proteinShares = {0.25, 0.35, 0.10, 0.30};

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCost = 0;

        double remainingBudget = request.getBudget();

        Set<String> usedFoods = new HashSet<>();

        /*
         * Pass 1:
         * Allocate foods toward calorie and protein targets.
         */
        for (int i = 0; i < meals.size(); i++) {

            MealSection meal = meals.get(i);

            double calorieTarget =
                    request.getDailyCalories() * calorieShares[i];

            double proteinTarget =
                    request.getDailyProtein() * proteinShares[i];

            double mealCalories = 0;
            double mealProtein = 0;
            double mealCost = 0;

            double maxPortion = i == 2 ? 100 : 150;

            for (Food food : foods) {

                if (remainingBudget < 0.01) {
                    break;
                }

                double caloriesNeeded =
                        calorieTarget - mealCalories;

                double proteinNeeded =
                        proteinTarget - mealProtein;

                if (caloriesNeeded <= 0 && proteinNeeded <= 0) {
                    break;
                }

                String name = normalize(food.getName());

                if (usedFoods.contains(name)
                        && hasUnusedAffordableFood(
                        foods, usedFoods, remainingBudget)) {
                    continue;
                }

                double caloriesPer100g = food.getCaloriesPer100g();
                double proteinPer100g = food.getProteinPer100g();
                double pricePer100g = food.getPricePer100g();

                double grams = Math.min(
                        maxPortion,
                        remainingBudget / pricePer100g * 100
                );

                /*
                 * If calories are still needed, limit the portion
                 * so it does not substantially overshoot this meal.
                 */
                if (caloriesNeeded > 0) {
                    grams = Math.min(
                            grams,
                            caloriesNeeded / caloriesPer100g * 100
                    );
                }

                /*
                 * Protein guides selection but is not a hard limit
                 * on all calorie intake.
                 */
                if (proteinNeeded > 0 && proteinPer100g > 0) {
                    double proteinLimitedGrams =
                            proteinNeeded / proteinPer100g * 100;

                    grams = Math.min(grams, proteinLimitedGrams + 5);
                }

                grams = Math.floor(grams);

                if (grams < 10) {
                    continue;
                }

                double calories =
                        grams / 100.0 * caloriesPer100g;

                double protein =
                        grams / 100.0 * proteinPer100g;

                double cost =
                        grams / 100.0 * pricePer100g;

                addFood(
                        meal, food, grams, calories, protein, cost
                );

                mealCalories += calories;
                mealProtein += protein;
                mealCost += cost;

                totalCalories += calories;
                totalProtein += protein;
                totalCost += cost;

                remainingBudget -= cost;
                usedFoods.add(name);
            }

            meal.setTotalCalories(round(mealCalories));
            meal.setTotalProtein(round(mealProtein));
            meal.setTotalCost(round(mealCost));
        }

        /*
         * Pass 2:
         * Close remaining calorie gaps using affordable foods.
         * Prefer foods with fewer calories from protein, so the
         * algorithm can add energy without excessive protein.
         */
        for (int i = 0; i < meals.size(); i++) {

            MealSection meal = meals.get(i);

            double calorieTarget =
                    request.getDailyCalories() * calorieShares[i];

            double mealCalories = meal.getTotalCalories();
            double mealProtein = meal.getTotalProtein();
            double mealCost = meal.getTotalCost();

            for (Food food : getCalorieFocusedFoods(foods)) {

                if (mealCalories >= calorieTarget * 0.98
                        || remainingBudget < 0.01) {
                    break;
                }

                double caloriesPer100g = food.getCaloriesPer100g();
                double proteinPer100g = food.getProteinPer100g();
                double pricePer100g = food.getPricePer100g();

                double caloriesNeeded =
                        calorieTarget - mealCalories;

                double grams = Math.min(
                        i == 2 ? 100 : 150,
                        remainingBudget / pricePer100g * 100
                );

                grams = Math.min(
                        grams,
                        caloriesNeeded / caloriesPer100g * 100
                );

                /*
                 * Avoid adding large amounts of protein once the
                 * overall protein target has already been reached.
                 */
                double proteinNeeded =
                        request.getDailyProtein() - totalProtein;

                if (proteinNeeded <= 0 && proteinPer100g > 0) {
                    double maximumExtraProtein =
                            Math.max(2, request.getDailyProtein() * 0.05);

                    grams = Math.min(
                            grams,
                            maximumExtraProtein / proteinPer100g * 100
                    );
                }

                grams = Math.floor(grams);

                if (grams < 10) {
                    continue;
                }

                double calories =
                        grams / 100.0 * caloriesPer100g;

                double protein =
                        grams / 100.0 * proteinPer100g;

                double cost =
                        grams / 100.0 * pricePer100g;

                addFood(
                        meal, food, grams, calories, protein, cost
                );

                mealCalories += calories;
                mealProtein += protein;
                mealCost += cost;

                totalCalories += calories;
                totalProtein += protein;
                totalCost += cost;

                remainingBudget -= cost;
                usedFoods.add(normalize(food.getName()));
            }

            meal.setTotalCalories(round(mealCalories));
            meal.setTotalProtein(round(mealProtein));
            meal.setTotalCost(round(mealCost));
        }

        /*
         * Pass 3:
         * If both nutrition targets are close to satisfied,
         * use some remaining budget to improve calorie coverage.
         * Never spend money just to exhaust the budget.
         */
        if (totalCalories >= request.getDailyCalories() * 0.95
                && totalProtein >= request.getDailyProtein()
                && remainingBudget > request.getBudget() * 0.05) {

            for (Food food : getCalorieFocusedFoods(foods)) {

                if (remainingBudget < 0.01) {
                    break;
                }

                double grams = Math.min(
                        100,
                        remainingBudget / food.getPricePer100g() * 100
                );

                grams = Math.floor(grams);

                if (grams < 10) {
                    continue;
                }

                double calories =
                        grams / 100.0 * food.getCaloriesPer100g();

                double protein =
                        grams / 100.0 * food.getProteinPer100g();

                double cost =
                        grams / 100.0 * food.getPricePer100g();

                if (totalCalories + calories
                        > request.getDailyCalories() * 1.05) {
                    continue;
                }

                if (totalProtein + protein
                        > request.getDailyProtein() * 1.10) {
                    continue;
                }

                MealSection dinner = meals.get(3);

                addFood(
                        dinner, food, grams, calories, protein, cost
                );

                dinner.setTotalCalories(
                        round(dinner.getTotalCalories() + calories)
                );

                dinner.setTotalProtein(
                        round(dinner.getTotalProtein() + protein)
                );

                dinner.setTotalCost(
                        round(dinner.getTotalCost() + cost)
                );

                totalCalories += calories;
                totalProtein += protein;
                totalCost += cost;

                remainingBudget -= cost;
            }
        }

        MealPlanResponse response = new MealPlanResponse();

        response.setMeals(meals);
        response.setTotalCalories(round(totalCalories));
        response.setTotalProtein(round(totalProtein));
        response.setTotalCost(round(totalCost));

        response.setTargetCalories(request.getDailyCalories());
        response.setTargetProtein(request.getDailyProtein());

        response.setCalorieTargetMet(
                totalCalories >= request.getDailyCalories() * 0.95
        );

        response.setProteinTargetMet(
                totalProtein >= request.getDailyProtein()
        );

        response.setBudgetMet(
                totalCost <= request.getBudget() + 0.01
        );

        return response;
    }

    private void addFood(
            MealSection meal,
            Food food,
            double grams,
            double calories,
            double protein,
            double cost) {

        meal.getFoods().add(
                new MealFoodItem(
                        food.getName(),
                        grams,
                        round(calories),
                        round(protein),
                        round(cost)
                )
        );
    }

    private List<Food> getCalorieFocusedFoods(List<Food> foods) {

        return foods.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (Food food) ->
                                        food.getCaloriesPer100g()
                                                / food.getPricePer100g()
                        ).reversed()
                )
                .toList();
    }

    private double scoreFood(Food food) {

        double caloriesPerRupee =
                food.getCaloriesPer100g()
                        / food.getPricePer100g();

        double proteinPerRupee =
                food.getProteinPer100g()
                        / food.getPricePer100g();

        return caloriesPerRupee * 0.5
                + proteinPerRupee * 0.5;
    }

    private boolean hasUnusedAffordableFood(
            List<Food> foods,
            Set<String> usedFoods,
            double remainingBudget) {

        return foods.stream().anyMatch(food ->
                !usedFoods.contains(normalize(food.getName()))
                        && food.getPricePer100g() > 0
                        && food.getPricePer100g() / 10.0
                        <= remainingBudget
        );
    }

    private void validateRequest(MealPlanRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Meal plan request is required."
            );
        }

        if (!Double.isFinite(request.getBudget())
                || request.getBudget() <= 0) {
            throw new IllegalArgumentException(
                    "Budget must be greater than zero."
            );
        }

        if (!Double.isFinite(request.getDailyCalories())
                || request.getDailyCalories() <= 0) {
            throw new IllegalArgumentException(
                    "Daily calorie target must be greater than zero."
            );
        }

        if (!Double.isFinite(request.getDailyProtein())
                || request.getDailyProtein() <= 0) {
            throw new IllegalArgumentException(
                    "Daily protein target must be greater than zero."
            );
        }
    }

    private String normalize(String name) {
        return name == null
                ? ""
                : name.trim().toLowerCase(Locale.ROOT);
    }

    private double getProteinPerRupee(Food food) {
        return food.getProteinPer100g()
                / food.getPricePer100g();
    }

    private boolean isAllowed(Food food, String requestedDiet) {

        if (requestedDiet == null || requestedDiet.isBlank()) {
            return true;
        }

        String diet = requestedDiet.trim()
                .toUpperCase(Locale.ROOT);

        String foodDiet = food.getDietType() == null
                ? ""
                : food.getDietType().trim()
                .toUpperCase(Locale.ROOT);

        if (diet.equals("VEGETARIAN")) {
            return foodDiet.equals("VEGETARIAN");
        }

        if (diet.equals("EGGETARIAN")) {
            return foodDiet.equals("VEGETARIAN")
                    || foodDiet.equals("EGGETARIAN");
        }

        if (diet.equals("NON_VEGETARIAN")) {
            return true;
        }

        return false;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}