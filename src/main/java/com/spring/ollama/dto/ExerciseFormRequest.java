package com.spring.ollama.dto;

// ========== ExerciseFormRequest.java ==========
public class ExerciseFormRequest {
    private String exerciseName;    // e.g., "bench press", "squat", "deadlift"

    public ExerciseFormRequest() {
    }

    public ExerciseFormRequest(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    @Override
    public String toString() {
        return "ExerciseFormRequest{exerciseName='" + exerciseName + "'}";
    }
}
