package com.spring.ollama.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Day Summary - Summary of completed day (for AI reference)
 */
@Document(collection = "day_summaries")
public class DaySummary {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private LocalDate date;

    private String dailyRoutineId; // Reference to DailyRoutine

    // Summary data
    private String overallPerformance; // EXCELLENT, GOOD, AVERAGE, POOR
    private String summary; // AI-generated or manual summary
    private Map<String, Object> metrics; // Flexible map for various metrics

    // What went well
    private List<String> achievements;

    // What could improve
    private List<String> improvements;

    // AI insights
    private String aiRecommendation;
    private String nextDayFocus;

    private LocalDateTime createdAt;

    // Constructors
    public DaySummary() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDailyRoutineId() { return dailyRoutineId; }
    public void setDailyRoutineId(String dailyRoutineId) { this.dailyRoutineId = dailyRoutineId; }

    public String getOverallPerformance() { return overallPerformance; }
    public void setOverallPerformance(String overallPerformance) {
        this.overallPerformance = overallPerformance;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }

    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }

    public String getAiRecommendation() { return aiRecommendation; }
    public void setAiRecommendation(String aiRecommendation) { this.aiRecommendation = aiRecommendation; }

    public String getNextDayFocus() { return nextDayFocus; }
    public void setNextDayFocus(String nextDayFocus) { this.nextDayFocus = nextDayFocus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
