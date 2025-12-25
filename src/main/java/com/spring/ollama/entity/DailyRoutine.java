package com.spring.ollama.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Daily Fitness Routine - Main entity for each day
 * One user can have many daily routines (one-to-many)
 */
@Document(collection = "daily_routines")
@CompoundIndexes({
        @CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'date': -1}")
})
public class DailyRoutine {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private LocalDate date; // The day this routine is for

    private String workoutPlanId; // Reference to WorkoutPlan
    private String mealPlanId;    // Reference to MealPlan
    private String previousDaySummaryId; // Reference to previous day's summary

    // Quick status indicators
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, SKIPPED
    private boolean workoutCompleted;
    private boolean mealPlanFollowed;

    // Progress tracking
    private DailyProgress progress;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // Nested class for daily progress
    public static class DailyProgress {
        private Integer workoutCompletionPercentage; // 0-100
        private Integer mealAdherence; // 0-100
        private Double caloriesConsumed;
        private Double caloriesBurned;
        private Integer waterIntake; // in ml
        private Integer sleepHours;
        private String mood; // EXCELLENT, GOOD, AVERAGE, POOR
        private String energyLevel; // HIGH, MEDIUM, LOW
        private List<String> notes; // User notes for the day

        // Getters and Setters
        public Integer getWorkoutCompletionPercentage() { return workoutCompletionPercentage; }
        public void setWorkoutCompletionPercentage(Integer workoutCompletionPercentage) {
            this.workoutCompletionPercentage = workoutCompletionPercentage;
        }

        public Integer getMealAdherence() { return mealAdherence; }
        public void setMealAdherence(Integer mealAdherence) { this.mealAdherence = mealAdherence; }

        public Double getCaloriesConsumed() { return caloriesConsumed; }
        public void setCaloriesConsumed(Double caloriesConsumed) { this.caloriesConsumed = caloriesConsumed; }

        public Double getCaloriesBurned() { return caloriesBurned; }
        public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

        public Integer getWaterIntake() { return waterIntake; }
        public void setWaterIntake(Integer waterIntake) { this.waterIntake = waterIntake; }

        public Integer getSleepHours() { return sleepHours; }
        public void setSleepHours(Integer sleepHours) { this.sleepHours = sleepHours; }

        public String getMood() { return mood; }
        public void setMood(String mood) { this.mood = mood; }

        public String getEnergyLevel() { return energyLevel; }
        public void setEnergyLevel(String energyLevel) { this.energyLevel = energyLevel; }

        public List<String> getNotes() { return notes; }
        public void setNotes(List<String> notes) { this.notes = notes; }
    }

    // Constructors
    public DailyRoutine() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "PLANNED";
        this.workoutCompleted = false;
        this.mealPlanFollowed = false;
        this.progress = new DailyProgress();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getWorkoutPlanId() { return workoutPlanId; }
    public void setWorkoutPlanId(String workoutPlanId) { this.workoutPlanId = workoutPlanId; }

    public String getMealPlanId() { return mealPlanId; }
    public void setMealPlanId(String mealPlanId) { this.mealPlanId = mealPlanId; }

    public String getPreviousDaySummaryId() { return previousDaySummaryId; }
    public void setPreviousDaySummaryId(String previousDaySummaryId) {
        this.previousDaySummaryId = previousDaySummaryId;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isWorkoutCompleted() { return workoutCompleted; }
    public void setWorkoutCompleted(boolean workoutCompleted) { this.workoutCompleted = workoutCompleted; }

    public boolean isMealPlanFollowed() { return mealPlanFollowed; }
    public void setMealPlanFollowed(boolean mealPlanFollowed) { this.mealPlanFollowed = mealPlanFollowed; }

    public DailyProgress getProgress() { return progress; }
    public void setProgress(DailyProgress progress) { this.progress = progress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}


