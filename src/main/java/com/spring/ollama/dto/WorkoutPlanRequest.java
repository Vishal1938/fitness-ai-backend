package com.spring.ollama.dto;


// ========== WorkoutPlanRequest.java ==========
public class WorkoutPlanRequest {
    private String goal;           // e.g., "muscle gain", "weight loss", "strength"
    private String experience;     // e.g., "beginner", "intermediate", "advanced"
    private String daysPerWeek;    // e.g., "3", "4", "5"

    private String AditionalInfoaboutWorkout;

    public String getAditionalInfoaboutWorkout() {
        return AditionalInfoaboutWorkout;
    }

    public void setAditionalInfoaboutWorkout(String aditionalInfoaboutWorkout) {
        AditionalInfoaboutWorkout = aditionalInfoaboutWorkout;
    }

    public WorkoutPlanRequest() {}

    public WorkoutPlanRequest(String goal, String experience, String daysPerWeek) {
        this.goal = goal;
        this.experience = experience;
        this.daysPerWeek = daysPerWeek;
    }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(String daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    @Override
    public String toString() {
        return "WorkoutPlanRequest{goal='" + goal + "', experience='" + experience +
                "', daysPerWeek='" + daysPerWeek + "'}";
    }
}

