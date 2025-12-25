package com.spring.ollama.repository;

import com.spring.ollama.entity.WorkoutPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Workout Plans
 */
@Repository
public interface WorkoutPlanRepository extends MongoRepository<WorkoutPlan, String> {

    // Find workout plan for a specific date
    Optional<WorkoutPlan> findByUserIdAndTargetDate(String userId, LocalDate targetDate);

    // Find all workout plans for a user
    List<WorkoutPlan> findByUserIdOrderByTargetDateDesc(String userId);

    // Find AI-generated plans
    List<WorkoutPlan> findByUserIdAndAiGeneratedOrderByTargetDateDesc(
            String userId, boolean aiGenerated);

    // Find plans by difficulty
    List<WorkoutPlan> findByUserIdAndDifficultyOrderByTargetDateDesc(
            String userId, String difficulty);

    // Find plans by focus area
    List<WorkoutPlan> findByUserIdAndFocusAreaOrderByTargetDateDesc(
            String userId, String focusArea);

    // Find plans in date range
    List<WorkoutPlan> findByUserIdAndTargetDateBetweenOrderByTargetDateDesc(
            String userId, LocalDate startDate, LocalDate endDate);
}