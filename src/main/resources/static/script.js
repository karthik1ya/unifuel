"use strict";

/* =========================================
   UNIFUEL — FRONTEND APPLICATION
========================================= */

const API = {
    foods: "/api/foods",
    nutrition: "/api/nutrition/calculate",
    mealPlan: "/api/meal-plans/recommend"
};

const state = {
    foods: [],
    filteredFoods: [],
    currentPage: 1,
    pageSize: 10
};

const $ = (id) => document.getElementById(id);


/* =========================================
   COMMON UTILITIES
========================================= */

async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            ...(options.body ? {
                "Content-Type": "application/json"
            } : {})
        }
    });

    if (!response.ok) {
        let message = `Request failed (${response.status})`;

        try {
            const body = await response.json();
            message = body.message || body.error || message;
        } catch {
            // Keep the HTTP status message.
        }

        throw new Error(message);
    }

    return response.json();
}


function showMessage(elementId, message, type = "error") {
    const element = $(elementId);

    if (!element) return;

    element.textContent = message;
    element.className = `message show ${type}`;
}


function clearMessage(elementId) {
    const element = $(elementId);

    if (!element) return;

    element.textContent = "";
    element.className = "message";
}


function formatNumber(value, digits = 1) {
    const number = Number(value);

    if (!Number.isFinite(number)) {
        return "—";
    }

    return number.toLocaleString("en-IN", {
        maximumFractionDigits: digits
    });
}


function safeNumber(value) {
    const number = Number(value);
    return Number.isFinite(number) ? number : 0;
}


function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}


/* =========================================
   1. NUTRITION CALCULATOR
========================================= */

const nutritionForm = $("nutrition-form");

if (nutritionForm) {
    nutritionForm.addEventListener("submit", async event => {
        event.preventDefault();

        clearMessage("nutrition-message");

        const button = event.submitter;

        if (button) button.disabled = true;

        const request = {
            age: Number($("age").value),
            weight: Number($("weight").value),
            height: Number($("height").value),
            gender: $("gender").value,
            fitnessGoal: $("fitnessGoal").value,
            activityLevel: $("activityLevel").value
        };

        if (
            !Number.isFinite(request.age) ||
            !Number.isFinite(request.weight) ||
            !Number.isFinite(request.height) ||
            request.age <= 0 ||
            request.weight <= 0 ||
            request.height <= 0
        ) {
            showMessage(
                "nutrition-message",
                "Enter valid positive values for age, weight, and height."
            );

            if (button) button.disabled = false;
            return;
        }

        try {
            const result = await apiRequest(API.nutrition, {
                method: "POST",
                body: JSON.stringify(request)
            });

            $("target-calories").textContent =
                formatNumber(result.targetCalories, 0);

            $("target-protein").textContent =
                formatNumber(result.targetProtein, 0);

            $("target-weight").textContent =
                formatNumber(request.weight, 1);

            // Automatically transfer calculated targets to the planner.
            $("dailyCalories").value =
                Math.round(result.targetCalories);

            $("dailyProtein").value =
                Math.round(result.targetProtein);

            showMessage(
                "nutrition-message",
                "Your estimated nutrition targets have been calculated.",
                "success"
            );

        } catch (error) {
            console.error("Nutrition calculation failed:", error);

            showMessage(
                "nutrition-message",
                `Could not calculate nutrition: ${error.message}`
            );

        } finally {
            if (button) button.disabled = false;
        }
    });
}


/* =========================================
   2. FOUR-MEAL DAILY PLANNER
========================================= */

const mealForm = $("meal-form");

if (mealForm) {
    mealForm.addEventListener("submit", async event => {
        event.preventDefault();

        clearMessage("meal-message");

        const button = event.submitter;

        if (button) button.disabled = true;

        const budget = Number($("budget").value);
        const dailyCalories = Number($("dailyCalories").value);
        const dailyProtein = Number($("dailyProtein").value);

        if (
            !Number.isFinite(budget) ||
            !Number.isFinite(dailyCalories) ||
            !Number.isFinite(dailyProtein) ||
            budget <= 0 ||
            dailyCalories <= 0 ||
            dailyProtein <= 0
        ) {
            showMessage(
                "meal-message",
                "Budget, calories, and protein must all be greater than zero."
            );

            if (button) button.disabled = false;
            return;
        }

        const request = {
            budget: budget,
            dailyCalories: dailyCalories,
            dailyProtein: dailyProtein,
            dietType: $("dietType").value,
            fitnessGoal: $("fitnessGoal").value
        };

        try {
            const result = await apiRequest(API.mealPlan, {
                method: "POST",
                body: JSON.stringify(request)
            });
            console.log("Meal plan result:", result);
            console.log("Number of meals:", result.meals?.length);
            console.log("First meal:", result.meals?.[0]);

            console.log("Meal plan API response:", result);

            renderMealPlan(result);

        } catch (error) {
            console.error("Meal planner request failed:", error);

            showMessage(
                "meal-message",
                `Could not load meal recommendations: ${error.message}`
            );

        } finally {
            if (button) button.disabled = false;
        }
    });
}


function renderMealPlan(result) {
    const tbody = $("meal-results");

    if (!tbody) {
        console.error("Missing HTML element: meal-results");
        return;
    }

    tbody.replaceChildren();

    const meals = Array.isArray(result.meals)
        ? result.meals
        : [];

    if (meals.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 5;
        cell.className = "empty-state";
        cell.textContent = "No meals were generated for this request.";

        row.appendChild(cell);
        tbody.appendChild(row);
    }

    meals.forEach(meal => {
        /* Meal heading */

        const headingRow = document.createElement("tr");
        const headingCell = document.createElement("td");

        headingCell.colSpan = 5;
        headingCell.textContent =
            `${getMealIcon(meal.name)} ${meal.name}`;

        headingCell.style.background = "#e9f7ed";
        headingCell.style.color = "#176b40";
        headingCell.style.fontWeight = "700";
        headingCell.style.padding = "12px 8px";

        headingRow.appendChild(headingCell);
        tbody.appendChild(headingRow);

        const foods = Array.isArray(meal.foods)
            ? meal.foods
            : [];

        /* Food rows */

        if (foods.length === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = 5;
            cell.className = "empty-state";
            cell.textContent = "No foods allocated to this meal.";

            row.appendChild(cell);
            tbody.appendChild(row);
        }

        foods.forEach(food => {
            const row = document.createElement("tr");

            addMealCell(row, food.foodName);
            addMealCell(
                row,
                `${formatNumber(food.quantityGrams, 0)} g`
            );
            addMealCell(row, formatNumber(food.calories));
            addMealCell(row, `${formatNumber(food.protein)} g`);
            addMealCell(row, `₹${formatNumber(food.cost, 2)}`);

            tbody.appendChild(row);
        });

        /* Meal totals */

        const totalRow = document.createElement("tr");
        const labelCell = document.createElement("td");

        labelCell.colSpan = 2;
        labelCell.textContent = `${meal.name} total`;
        labelCell.style.fontWeight = "700";

        totalRow.appendChild(labelCell);

        addMealCell(
            totalRow,
            formatNumber(meal.totalCalories, 0),
            true
        );

        addMealCell(
            totalRow,
            `${formatNumber(meal.totalProtein)} g`,
            true
        );

        addMealCell(
            totalRow,
            `₹${formatNumber(meal.totalCost, 2)}`,
            true
        );

        tbody.appendChild(totalRow);
    });

    /* Daily totals */

    $("meal-total-calories").textContent =
        formatNumber(result.totalCalories, 0);

    $("meal-total-protein").textContent =
        `${formatNumber(result.totalProtein)} g`;

    $("meal-total-cost").textContent =
        `₹${formatNumber(result.totalCost, 2)}`;

    showMealPlanStatus(result);
}


function addMealCell(row, value, bold = false) {
    const cell = document.createElement("td");

    cell.textContent = String(value ?? "—");

    if (bold) {
        cell.style.fontWeight = "700";
    }

    row.appendChild(cell);
}


function getMealIcon(name) {
    const meal = String(name || "").toLowerCase();

    if (meal.includes("breakfast")) return "🌅";
    if (meal.includes("lunch")) return "☀️";
    if (meal.includes("pre-workout")) return "⚡";
    if (meal.includes("dinner")) return "🌙";

    return "🍽️";
}


function showMealPlanStatus(result) {
    const warnings = [];

    if (result.calorieTargetMet === false) {
        warnings.push("Calorie target not met");
    }

    if (result.proteinTargetMet === false) {
        warnings.push("Protein target not met");
    }

    if (result.budgetMet === false) {
        warnings.push("Budget exceeded");
    }

    if (warnings.length > 0) {
        showMessage(
            "meal-message",
            `Plan generated, but ${warnings.join("; ")}.`
        );
    } else {
        showMessage(
            "meal-message",
            "Four-meal plan generated. Review the food quantities and daily totals.",
            "success"
        );
    }
}


/* =========================================
   3. FOOD EXPLORER
========================================= */

async function loadFoods() {
    const tbody = $("food-results");

    if (!tbody) return;

    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="empty-state">
                Loading foods...
            </td>
        </tr>
    `;

    try {
        const result = await apiRequest(API.foods);

        state.foods = Array.isArray(result) ? result : [];

        applyFoodFilters();

        $("food-count").textContent =
            `${state.foods.length} food records loaded from the database.`;

    } catch (error) {
        console.error("Food loading failed:", error);

        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    Could not load foods:
                    ${escapeHtml(error.message)}
                </td>
            </tr>
        `;

        $("food-count").textContent =
            "Food database unavailable. Check that Spring Boot is running.";
    }
}


function applyFoodFilters() {
    const query = $("food-search").value.trim().toLowerCase();
    const diet = $("food-diet").value;
    const sort = $("food-sort").value;

    state.filteredFoods = state.foods.filter(food => {
        const matchesName = String(food.name ?? "")
            .toLowerCase()
            .includes(query);

        const matchesDiet = !diet ||
            String(food.dietType ?? "").toUpperCase() === diet;

        return matchesName && matchesDiet;
    });

    state.filteredFoods.sort((a, b) => {
        switch (sort) {
            case "protein":
                return safeNumber(b.proteinPer100g) -
                    safeNumber(a.proteinPer100g);

            case "price":
                return safeNumber(a.pricePer100g) -
                    safeNumber(b.pricePer100g);

            case "calories":
                return safeNumber(a.caloriesPer100g) -
                    safeNumber(b.caloriesPer100g);

            default:
                return String(a.name ?? "").localeCompare(
                    String(b.name ?? "")
                );
        }
    });

    state.currentPage = 1;

    renderFoods();
}


function renderFoods() {
    const tbody = $("food-results");

    if (!tbody) return;

    tbody.replaceChildren();

    const total = state.filteredFoods.length;

    const totalPages = Math.max(
        1,
        Math.ceil(total / state.pageSize)
    );

    state.currentPage = Math.min(
        state.currentPage,
        totalPages
    );

    const start = (state.currentPage - 1) * state.pageSize;

    const pageFoods = state.filteredFoods.slice(
        start,
        start + state.pageSize
    );

    if (pageFoods.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 7;
        cell.className = "empty-state";
        cell.textContent = "No foods match your search.";

        row.appendChild(cell);
        tbody.appendChild(row);
    }

    pageFoods.forEach(food => {
        const row = document.createElement("tr");

        addFoodCell(row, food.name);
        addFoodCell(row, formatNumber(food.caloriesPer100g));
        addFoodCell(row, formatNumber(food.proteinPer100g));
        addFoodCell(row, formatNumber(food.carbohydratesPer100g));
        addFoodCell(row, formatNumber(food.fatPer100g));
        addFoodCell(row, `₹${formatNumber(food.pricePer100g, 2)}`);

        const dietCell = document.createElement("td");
        const badge = document.createElement("span");

        const diet = String(food.dietType ?? "").toUpperCase();

        badge.className = "diet-badge";

        if (diet === "EGGETARIAN") {
            badge.classList.add("egg");
            badge.textContent = "E";
        } else if (diet === "NON_VEGETARIAN") {
            badge.classList.add("nonveg");
            badge.textContent = "NV";
        } else {
            badge.textContent = "V";
        }

        dietCell.appendChild(badge);
        row.appendChild(dietCell);

        tbody.appendChild(row);
    });

    $("page-indicator").textContent =
        `Page ${state.currentPage} of ${totalPages}`;

    $("previous-page").disabled = state.currentPage <= 1;
    $("next-page").disabled = state.currentPage >= totalPages;

    $("food-count").textContent =
        `${total} matching foods · ${state.foods.length} total in database`;
}


function addFoodCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = String(value ?? "—");
    row.appendChild(cell);
}


/* =========================================
   4. FOOD EXPLORER EVENTS
========================================= */

if ($("food-search")) {
    $("food-search").addEventListener(
        "input",
        applyFoodFilters
    );
}

if ($("food-diet")) {
    $("food-diet").addEventListener(
        "change",
        applyFoodFilters
    );
}

if ($("food-sort")) {
    $("food-sort").addEventListener(
        "change",
        applyFoodFilters
    );
}

if ($("previous-page")) {
    $("previous-page").addEventListener("click", () => {
        if (state.currentPage > 1) {
            state.currentPage--;
            renderFoods();
        }
    });
}

if ($("next-page")) {
    $("next-page").addEventListener("click", () => {
        const totalPages = Math.max(
            1,
            Math.ceil(state.filteredFoods.length / state.pageSize)
        );

        if (state.currentPage < totalPages) {
            state.currentPage++;
            renderFoods();
        }
    });
}


/* =========================================
   5. INITIALIZE APPLICATION
========================================= */

document.addEventListener("DOMContentLoaded", () => {
    loadFoods();

    // Calculate initial nutrition targets from the form defaults.
    if ($("nutrition-form")) {
        $("nutrition-form").requestSubmit();
    }
});