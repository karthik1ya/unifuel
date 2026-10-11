package com.unifuel;

import com.unifuel.entity.Food;
import com.unifuel.repository.FoodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadFoodData(FoodRepository foodRepository) {
        return args -> {

            List<Food> foods = List.of(

                    // 1–15: Existing foods
                    new Food("Oats", 389, 16.9, 66.3, 6.9, 12, "VEGETARIAN"),
                    new Food("Milk", 61, 3.2, 4.8, 3.3, 7, "VEGETARIAN"),
                    new Food("Paneer", 265, 18.3, 1.2, 20.8, 80, "VEGETARIAN"),
                    new Food("Soya Chunks", 345, 52, 33, 0.5, 15, "VEGETARIAN"),
                    new Food("Boiled Chickpeas", 164, 8.9, 27.4, 2.6, 12, "VEGETARIAN"),
                    new Food("Cooked Lentils", 116, 9, 20.1, 0.4, 10, "VEGETARIAN"),
                    new Food("Curd", 61, 3.5, 4.7, 3.3, 8, "VEGETARIAN"),
                    new Food("Peanuts", 567, 25.8, 16.1, 49.2, 12, "VEGETARIAN"),
                    new Food("Banana", 89, 1.1, 22.8, 0.3, 6, "VEGETARIAN"),
                    new Food("Cooked Rice", 130, 2.7, 28.2, 0.3, 6, "VEGETARIAN"),
                    new Food("Boiled Egg", 155, 12.6, 1.1, 10.6, 10, "EGGETARIAN"),
                    new Food("Chicken Breast", 165, 31, 0, 3.6, 25, "NON_VEGETARIAN"),
                    new Food("Tofu", 76, 8, 1.9, 4.8, 25, "VEGETARIAN"),
                    new Food("Potato", 87, 1.9, 20.1, 0.1, 5, "VEGETARIAN"),
                    new Food("Whole Wheat Roti", 297, 9.6, 46.1, 7.4, 8, "VEGETARIAN"),

                    // 16–30: Pulses and legumes
                    new Food("Boiled Kidney Beans", 127, 8.7, 22.8, 0.5, 12, "VEGETARIAN"),
                    new Food("Boiled Green Gram", 105, 7, 19.2, 0.4, 12, "VEGETARIAN"),
                    new Food("Boiled Black Chickpeas", 164, 8.9, 27.4, 2.6, 12, "VEGETARIAN"),
                    new Food("Boiled Black Eyed Peas", 116, 7.7, 20.8, 0.5, 12, "VEGETARIAN"),
                    new Food("Boiled Pigeon Peas", 121, 6.8, 23.3, 1.0, 12, "VEGETARIAN"),
                    new Food("Boiled Moth Beans", 117, 7.5, 20, 1, 12, "VEGETARIAN"),
                    new Food("Boiled Soybeans", 172, 18.2, 8.4, 9, 18, "VEGETARIAN"),
                    new Food("Roasted Gram", 369, 22, 58, 6, 14, "VEGETARIAN"),
                    new Food("Sattu", 400, 20, 60, 7, 12, "VEGETARIAN"),
                    new Food("Split Yellow Moong Dal Dry", 347, 24, 63, 1.2, 14, "VEGETARIAN"),
                    new Food("Chana Dal Dry", 360, 22, 60, 5, 14, "VEGETARIAN"),
                    new Food("Toor Dal Dry", 343, 22, 63, 1.5, 14, "VEGETARIAN"),
                    new Food("Masoor Dal Dry", 352, 25, 60, 1.1, 14, "VEGETARIAN"),
                    new Food("Urad Dal Dry", 341, 25, 59, 1.6, 14, "VEGETARIAN"),
                    new Food("Green Peas", 81, 5.4, 14.5, 0.4, 10, "VEGETARIAN"),

                    // 31–40: Dairy and alternatives
                    new Food("Buttermilk", 40, 3.3, 4.8, 0.9, 5, "VEGETARIAN"),
                    new Food("Greek Yogurt", 97, 9, 3.9, 5, 18, "VEGETARIAN"),
                    new Food("Cheese", 402, 25, 1.3, 33, 60, "VEGETARIAN"),
                    new Food("Skimmed Milk", 35, 3.4, 5, 0.1, 8, "VEGETARIAN"),
                    new Food("Low Fat Curd", 63, 5.3, 7, 1.6, 10, "VEGETARIAN"),
                    new Food("Soy Milk", 33, 3.3, 1.8, 1.8, 12, "VEGETARIAN"),
                    new Food("Lassi Unsweetened", 60, 3, 5, 3, 10, "VEGETARIAN"),
                    new Food("Whey Protein Powder", 400, 80, 10, 5, 120, "VEGETARIAN"),
                    new Food("Mozzarella Cheese", 280, 28, 3.1, 17, 70, "VEGETARIAN"),
                    new Food("Low Fat Paneer", 170, 20, 4, 8, 90, "VEGETARIAN"),

                    // 41–50: Eggs and non-vegetarian foods
                    new Food("Boiled Egg White", 52, 10.9, 0.7, 0.2, 10, "EGGETARIAN"),
                    new Food("Egg Omelette", 154, 11, 1.5, 11, 12, "EGGETARIAN"),
                    new Food("Chicken Thigh Cooked", 209, 26, 0, 10.9, 22, "NON_VEGETARIAN"),
                    new Food("Turkey Breast Cooked", 135, 29, 0, 1.5, 45, "NON_VEGETARIAN"),
                    new Food("Rohu Fish Cooked", 150, 22, 0, 6, 20, "NON_VEGETARIAN"),
                    new Food("Tuna Canned", 116, 26, 0, 1, 65, "NON_VEGETARIAN"),
                    new Food("Sardines Cooked", 208, 25, 0, 11, 30, "NON_VEGETARIAN"),
                    new Food("Prawns Cooked", 99, 24, 0.2, 0.3, 35, "NON_VEGETARIAN"),
                    new Food("Salmon Cooked", 206, 22, 0, 12, 100, "NON_VEGETARIAN"),
                    new Food("Chicken Liver Cooked", 167, 24, 1.1, 6.5, 18, "NON_VEGETARIAN"),

                    // 51–60: Grains and cereals
                    new Food("Brown Rice Cooked", 123, 2.7, 25.6, 1, 10, "VEGETARIAN"),
                    new Food("Quinoa Cooked", 120, 4.4, 21.3, 1.9, 35, "VEGETARIAN"),
                    new Food("Ragi Flour", 336, 7.3, 72, 1.3, 12, "VEGETARIAN"),
                    new Food("Jowar Flour", 329, 10.4, 72.6, 3.1, 12, "VEGETARIAN"),
                    new Food("Bajra Flour", 361, 11.6, 67.5, 5, 12, "VEGETARIAN"),
                    new Food("Whole Wheat Flour", 340, 13.2, 72, 2.5, 8, "VEGETARIAN"),
                    new Food("Poha Dry", 350, 6.7, 77, 1, 10, "VEGETARIAN"),
                    new Food("Upma Cooked", 120, 3, 20, 3, 10, "VEGETARIAN"),
                    new Food("Idli", 146, 4.5, 30, 0.7, 8, "VEGETARIAN"),
                    new Food("Dosa Plain", 168, 4, 29, 3.7, 10, "VEGETARIAN"),

                    // 61–70: Fruits
                    new Food("Apple", 52, 0.3, 13.8, 0.2, 12, "VEGETARIAN"),
                    new Food("Orange", 47, 0.9, 11.8, 0.1, 10, "VEGETARIAN"),
                    new Food("Guava", 68, 2.6, 14.3, 1, 10, "VEGETARIAN"),
                    new Food("Papaya", 43, 0.5, 10.8, 0.3, 8, "VEGETARIAN"),
                    new Food("Mango", 60, 0.8, 15, 0.4, 12, "VEGETARIAN"),
                    new Food("Pomegranate", 83, 1.7, 18.7, 1.2, 18, "VEGETARIAN"),
                    new Food("Watermelon", 30, 0.6, 7.6, 0.2, 5, "VEGETARIAN"),
                    new Food("Pineapple", 50, 0.5, 13.1, 0.1, 10, "VEGETARIAN"),
                    new Food("Grapes", 69, 0.7, 18.1, 0.2, 12, "VEGETARIAN"),
                    new Food("Dates", 282, 2.5, 75, 0.4, 30, "VEGETARIAN"),

                    // 71–80: Vegetables
                    new Food("Spinach", 23, 2.9, 3.6, 0.4, 8, "VEGETARIAN"),
                    new Food("Broccoli", 34, 2.8, 6.6, 0.4, 25, "VEGETARIAN"),
                    new Food("Carrot", 41, 0.9, 9.6, 0.2, 8, "VEGETARIAN"),
                    new Food("Tomato", 18, 0.9, 3.9, 0.2, 6, "VEGETARIAN"),
                    new Food("Cucumber", 15, 0.7, 3.6, 0.1, 5, "VEGETARIAN"),
                    new Food("Onion", 40, 1.1, 9.3, 0.1, 5, "VEGETARIAN"),
                    new Food("Cauliflower", 25, 1.9, 5, 0.3, 10, "VEGETARIAN"),
                    new Food("Cabbage", 25, 1.3, 5.8, 0.1, 8, "VEGETARIAN"),
                    new Food("Beetroot", 43, 1.6, 9.6, 0.2, 10, "VEGETARIAN"),
                    new Food("Sweet Potato", 86, 1.6, 20.1, 0.1, 8, "VEGETARIAN"),

                    // 81–90: Nuts and seeds
                    new Food("Almonds", 579, 21.2, 21.6, 49.9, 80, "VEGETARIAN"),
                    new Food("Cashews", 553, 18.2, 30.2, 43.9, 80, "VEGETARIAN"),
                    new Food("Walnuts", 654, 15.2, 13.7, 65.2, 100, "VEGETARIAN"),
                    new Food("Pumpkin Seeds", 559, 30.2, 10.7, 49.1, 70, "VEGETARIAN"),
                    new Food("Sunflower Seeds", 584, 20.8, 20, 51.5, 40, "VEGETARIAN"),
                    new Food("Chia Seeds", 486, 16.5, 42.1, 30.7, 80, "VEGETARIAN"),
                    new Food("Flax Seeds", 534, 18.3, 28.9, 42.2, 30, "VEGETARIAN"),
                    new Food("Sesame Seeds", 573, 17.7, 23.4, 49.7, 30, "VEGETARIAN"),
                    new Food("Pistachios", 562, 20.2, 27.2, 45.3, 100, "VEGETARIAN"),
                    new Food("Peanut Butter", 588, 25, 20, 50, 35, "VEGETARIAN"),

                    // 91–100: Other affordable foods
                    new Food("Besan", 387, 22, 58, 7, 10, "VEGETARIAN"),
                    new Food("Sprouted Moong", 30, 3, 6, 0.2, 12, "VEGETARIAN"),
                    new Food("Boiled Corn", 96, 3.4, 21, 1.5, 10, "VEGETARIAN"),
                    new Food("Mushrooms", 22, 3.1, 3.3, 0.3, 20, "VEGETARIAN"),
                    new Food("Pumpkin", 26, 1, 6.5, 0.1, 8, "VEGETARIAN"),
                    new Food("Brinjal", 25, 1, 6, 0.2, 8, "VEGETARIAN"),
                    new Food("Okra", 33, 1.9, 7.5, 0.2, 10, "VEGETARIAN"),
                    new Food("Green Beans", 31, 1.8, 7, 0.2, 12, "VEGETARIAN"),
                    new Food("Coconut Fresh", 354, 3.3, 15.2, 33.5, 25, "VEGETARIAN"),
                    new Food("Jaggery", 383, 0.4, 98, 0.1, 10, "VEGETARIAN")
            );

            // Insert only foods whose names do not already exist.
            Map<String, Food> existingFoods = foodRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(
                            food -> food.getName().trim().toLowerCase(),
                            Function.identity(),
                            (first, second) -> first
                    ));

            List<Food> missingFoods = foods.stream()
                    .filter(food -> !existingFoods.containsKey(
                            food.getName().trim().toLowerCase()
                    ))
                    .toList();

            if (!missingFoods.isEmpty()) {
                foodRepository.saveAll(missingFoods);
            }

            System.out.println(
                    "UniFuel: Added " + missingFoods.size()
                            + " new foods. Total foods in database: "
                            + foodRepository.count()
            );
        };
    }
}