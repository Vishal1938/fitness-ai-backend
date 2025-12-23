package com.spring.ollama.dto;

/**
 * Response DTO containing the complete fitness plan
 */
public class CompleteFitnessPlanResponse {

    private String workoutPlan;
    private String mealPlan;
    private String supplementRecommendations;
    private String summary;
    private long processingTimeMs;

    // Default constructor
    public CompleteFitnessPlanResponse() {}

    // Constructor with all fields
    public CompleteFitnessPlanResponse(String workoutPlan, String mealPlan,
                                       String supplementRecommendations, String summary,
                                       long processingTimeMs) {
        this.workoutPlan = workoutPlan;
        this.mealPlan = mealPlan;
        this.supplementRecommendations = supplementRecommendations;
        this.summary = summary;
        this.processingTimeMs = processingTimeMs;
    }

    // Getters and Setters
    public String getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(String workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    public String getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(String mealPlan) {
        this.mealPlan = mealPlan;
    }

    public String getSupplementRecommendations() {
        return supplementRecommendations;
    }

    public void setSupplementRecommendations(String supplementRecommendations) {
        this.supplementRecommendations = supplementRecommendations;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    @Override
    public String toString() {
        return "CompleteFitnessPlanResponse{" +
                "workoutPlan='" + workoutPlan + '\'' +
                ", mealPlan='" + mealPlan + '\'' +
                ", supplementRecommendations='" + supplementRecommendations + '\'' +
                ", summary='" + summary + '\'' +
                ", processingTimeMs=" + processingTimeMs +
                '}';
    }
}