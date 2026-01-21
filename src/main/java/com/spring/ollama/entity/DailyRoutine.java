// ==========================================
// 1. DailyRoutine Entity (Simple Version)
// ==========================================
package com.spring.ollama.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "daily_routines")
public class DailyRoutine {

    @Id
    private String id;

    @Indexed
    private String userId;

    private int dayNumber;  // 1, 2, 3, etc.

    // Store the entire structured JSON response from LLM
    private String structuredPlanJson;

    private boolean isCompleted;
    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public DailyRoutine() {
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getStructuredPlanJson() {
        return structuredPlanJson;
    }

    public void setStructuredPlanJson(String structuredPlanJson) {
        this.structuredPlanJson = structuredPlanJson;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}