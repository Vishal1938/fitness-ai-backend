package com.spring.ollama.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Meal Plan - Detailed meal plan for a specific day
 */
@Document(collection = "meal_plans")
public class MealPlan {

    @Id
    private String id;

    @Indexed
    private String userId;

    private LocalDate targetDate;
    private String planName;
    private String dietType; // BALANCED, KETO, VEGAN, VEGETARIAN, HIGH_PROTEIN
    private Integer targetCalories;

    private List<Meal> meals;

    private NutritionSummary nutritionSummary;

    // AI generation metadata
    private boolean aiGenerated;
    private String generationPrompt;
    private LocalDateTime generatedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested Meal class
    public static class Meal {
        private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK
        private String name;
        private List<FoodItem> items;
        private Integer calories;
        private String preparationTime;
        private String instructions;
        private boolean consumed;
        private LocalDateTime consumedAt;
        private String notes;

        // Getters and Setters
        public String getMealType() { return mealType; }
        public void setMealType(String mealType) { this.mealType = mealType; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<FoodItem> getItems() { return items; }
        public void setItems(List<FoodItem> items) { this.items = items; }

        public Integer getCalories() { return calories; }
        public void setCalories(Integer calories) { this.calories = calories; }

        public String getPreparationTime() { return preparationTime; }
        public void setPreparationTime(String preparationTime) { this.preparationTime = preparationTime; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }

        public boolean isConsumed() { return consumed; }
        public void setConsumed(boolean consumed) { this.consumed = consumed; }

        public LocalDateTime getConsumedAt() { return consumedAt; }
        public void setConsumedAt(LocalDateTime consumedAt) { this.consumedAt = consumedAt; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    // Nested FoodItem class
    public static class FoodItem {
        private String name;
        private String quantity;
        private Integer calories;
        private Double protein; // in grams
        private Double carbs;   // in grams
        private Double fats;    // in grams
        private Double fiber;   // in grams

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }

        public Integer getCalories() { return calories; }
        public void setCalories(Integer calories) { this.calories = calories; }

        public Double getProtein() { return protein; }
        public void setProtein(Double protein) { this.protein = protein; }

        public Double getCarbs() { return carbs; }
        public void setCarbs(Double carbs) { this.carbs = carbs; }

        public Double getFats() { return fats; }
        public void setFats(Double fats) { this.fats = fats; }

        public Double getFiber() { return fiber; }
        public void setFiber(Double fiber) { this.fiber = fiber; }
    }

    // Nested NutritionSummary class
    public static class NutritionSummary {
        private Integer totalCalories;
        private Double totalProtein;
        private Double totalCarbs;
        private Double totalFats;
        private Double totalFiber;

        // Getters and Setters
        public Integer getTotalCalories() { return totalCalories; }
        public void setTotalCalories(Integer totalCalories) { this.totalCalories = totalCalories; }

        public Double getTotalProtein() { return totalProtein; }
        public void setTotalProtein(Double totalProtein) { this.totalProtein = totalProtein; }

        public Double getTotalCarbs() { return totalCarbs; }
        public void setTotalCarbs(Double totalCarbs) { this.totalCarbs = totalCarbs; }

        public Double getTotalFats() { return totalFats; }
        public void setTotalFats(Double totalFats) { this.totalFats = totalFats; }

        public Double getTotalFiber() { return totalFiber; }
        public void setTotalFiber(Double totalFiber) { this.totalFiber = totalFiber; }
    }

    // Constructors
    public MealPlan() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }

    public Integer getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Integer targetCalories) { this.targetCalories = targetCalories; }

    public List<Meal> getMeals() { return meals; }
    public void setMeals(List<Meal> meals) { this.meals = meals; }

    public NutritionSummary getNutritionSummary() { return nutritionSummary; }
    public void setNutritionSummary(NutritionSummary nutritionSummary) {
        this.nutritionSummary = nutritionSummary;
    }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }

    public String getGenerationPrompt() { return generationPrompt; }
    public void setGenerationPrompt(String generationPrompt) { this.generationPrompt = generationPrompt; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
