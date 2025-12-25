package com.spring.ollama.repository;

import com.spring.ollama.entity.MealPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Meal Plans
 */
@Repository
public interface MealPlanRepository extends MongoRepository<MealPlan, String> {

    // Find meal plan for a specific date
    Optional<MealPlan> findByUserIdAndTargetDate(String userId, LocalDate targetDate);

    // Find all meal plans for a user
    List<MealPlan> findByUserIdOrderByTargetDateDesc(String userId);

    // Find AI-generated plans
    List<MealPlan> findByUserIdAndAiGeneratedOrderByTargetDateDesc(
            String userId, boolean aiGenerated);

    // Find plans by diet type
    List<MealPlan> findByUserIdAndDietTypeOrderByTargetDateDesc(
            String userId, String dietType);

    // Find plans in date range
    List<MealPlan> findByUserIdAndTargetDateBetweenOrderByTargetDateDesc(
            String userId, LocalDate startDate, LocalDate endDate);

    // Find plans within calorie range
    @Query("{ 'userId': ?0, 'targetCalories': { $gte: ?1, $lte: ?2 } }")
    List<MealPlan> findByUserIdAndCalorieRange(
            String userId, Integer minCalories, Integer maxCalories);
}