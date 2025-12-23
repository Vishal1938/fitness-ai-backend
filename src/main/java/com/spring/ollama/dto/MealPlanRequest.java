package com.spring.ollama.dto;

// ========== MealPlanRequest.java ==========
public class MealPlanRequest {
    private String goal;                  // e.g., "muscle gain", "fat loss", "maintenance"
    private String calories;              // e.g., "2000", "2500", "3000"
    private String dietaryPreference;     // e.g., "vegetarian", "vegan", "keto", "balanced"

    private String aditionalInfoaboutmeal;

    public String getAditionalInfoaboutmeal() {
        return aditionalInfoaboutmeal;
    }

    public void setAditionalInfoaboutmeal(String aditionalInfoaboutmeal) {
        this.aditionalInfoaboutmeal = aditionalInfoaboutmeal;
    }

    public MealPlanRequest() {
    }

    public MealPlanRequest(String goal, String calories, String dietaryPreference) {
        this.goal = goal;
        this.calories = calories;
        this.dietaryPreference = dietaryPreference;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    @Override
    public String toString() {
        return "MealPlanRequest{goal='" + goal + "', calories='" + calories +
                "', dietaryPreference='" + dietaryPreference + "'}";
    }

    // ========== SupplementRequest.java ==========
}
