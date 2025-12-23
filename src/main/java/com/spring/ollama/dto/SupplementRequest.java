package com.spring.ollama.dto;

/**
 * Request DTO for getting supplement recommendations
 */
public class SupplementRequest {
    private String goal;        // e.g., "muscle gain", "endurance", "recovery"
    private String dietType;    // e.g., "omnivore", "vegetarian", "vegan"

    private String aditionalInstructionaboutSuplement;

    public String getAditionalInstructionaboutSuplement() {
        return aditionalInstructionaboutSuplement;
    }

    public void setAditionalInstructionaboutSuplement(String aditionalInstructionaboutSuplement) {
        this.aditionalInstructionaboutSuplement = aditionalInstructionaboutSuplement;
    }

    public SupplementRequest() {}

    public SupplementRequest(String goal, String dietType) {
        this.goal = goal;
        this.dietType = dietType;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    @Override
    public String toString() {
        return "SupplementRequest{" +
                "goal='" + goal + '\'' +
                ", dietType='" + dietType + '\'' +
                '}';
    }
}