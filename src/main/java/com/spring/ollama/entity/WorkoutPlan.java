package com.spring.ollama.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Workout Plan - Detailed workout for a specific day
 */
@Document(collection = "workout_plans")
public class WorkoutPlan {

    @Id
    private String id;

    @Indexed
    private String userId;

    private LocalDate targetDate;
    private String planName;
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    private String focusArea; // STRENGTH, CARDIO, FLEXIBILITY, MIXED
    private Integer estimatedDuration; // in minutes

    private List<Exercise> exercises;

    // AI generation metadata
    private boolean aiGenerated;
    private String generationPrompt;
    private LocalDateTime generatedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested Exercise class
    public static class Exercise {
        private String name;
        private String category; // WARMUP, MAIN, COOLDOWN
        private String type; // CARDIO, STRENGTH, FLEXIBILITY
        private Integer sets;
        private Integer reps;
        private Integer duration; // in seconds
        private Integer restTime; // in seconds
        private String equipment; // NONE, DUMBBELLS, BARBELL, etc.
        private String instructions;
        private String videoUrl;
        private boolean completed;
        private String notes;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Integer getSets() { return sets; }
        public void setSets(Integer sets) { this.sets = sets; }

        public Integer getReps() { return reps; }
        public void setReps(Integer reps) { this.reps = reps; }

        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }

        public Integer getRestTime() { return restTime; }
        public void setRestTime(Integer restTime) { this.restTime = restTime; }

        public String getEquipment() { return equipment; }
        public void setEquipment(String equipment) { this.equipment = equipment; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }

        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    // Constructors
    public WorkoutPlan() {
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

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

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
