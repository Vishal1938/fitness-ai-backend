package com.spring.ollama.service;

import com.spring.ollama.entity.*;
import com.spring.ollama.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyRoutineService {

    private static final Logger logger = LoggerFactory.getLogger(DailyRoutineService.class);

    private final DailyRoutineRepository dailyRoutineRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final MealPlanRepository mealPlanRepository;
    private final DaySummaryRepository daySummaryRepository;

    public DailyRoutineService(
            DailyRoutineRepository dailyRoutineRepository,
            WorkoutPlanRepository workoutPlanRepository,
            MealPlanRepository mealPlanRepository,
            DaySummaryRepository daySummaryRepository) {
        this.dailyRoutineRepository = dailyRoutineRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.daySummaryRepository = daySummaryRepository;
    }

    /**
     * Get or create today's routine for a user
     */
    public DailyRoutine getTodayRoutine(String userId) {
        LocalDate today = LocalDate.now();

        Optional<DailyRoutine> existing = dailyRoutineRepository.findByUserIdAndDate(userId, today);

        if (existing.isPresent()) {
            return existing.get();
        }

        // Create new routine for today
        return createDailyRoutine(userId, today);
    }

    /**
     * Create a new daily routine
     */
    public DailyRoutine createDailyRoutine(String userId, LocalDate date) {
        logger.info("Creating daily routine for user: {} on date: {}", userId, date);

        // Check if routine already exists
        if (dailyRoutineRepository.existsByUserIdAndDate(userId, date)) {
            throw new IllegalStateException("Routine already exists for this date");
        }

        DailyRoutine routine = new DailyRoutine();
        routine.setUserId(userId);
        routine.setDate(date);

        // Link previous day's summary if exists
        LocalDate yesterday = date.minusDays(1);
        Optional<DaySummary> yesterdaySummary = daySummaryRepository
                .findByUserIdAndDate(userId, yesterday);

        yesterdaySummary.ifPresent(summary ->
                routine.setPreviousDaySummaryId(summary.getId()));

        return dailyRoutineRepository.save(routine);
    }

    /**
     * Create and attach workout plan to routine
     */
    public WorkoutPlan createWorkoutPlan(String userId, LocalDate targetDate,
                                         WorkoutPlan workoutPlan) {
        logger.info("Creating workout plan for user: {} on date: {}", userId, targetDate);

        workoutPlan.setUserId(userId);
        workoutPlan.setTargetDate(targetDate);
        workoutPlan.setCreatedAt(LocalDateTime.now());
        workoutPlan.setUpdatedAt(LocalDateTime.now());

        WorkoutPlan savedPlan = workoutPlanRepository.save(workoutPlan);

        // Update daily routine with workout plan ID
        Optional<DailyRoutine> routineOpt = dailyRoutineRepository
                .findByUserIdAndDate(userId, targetDate);

        if (routineOpt.isPresent()) {
            DailyRoutine routine = routineOpt.get();
            routine.setWorkoutPlanId(savedPlan.getId());
            routine.setUpdatedAt(LocalDateTime.now());
            dailyRoutineRepository.save(routine);
        }

        return savedPlan;
    }

    /**
     * Create and attach meal plan to routine
     */
    public MealPlan createMealPlan(String userId, LocalDate targetDate, MealPlan mealPlan) {
        logger.info("Creating meal plan for user: {} on date: {}", userId, targetDate);

        mealPlan.setUserId(userId);
        mealPlan.setTargetDate(targetDate);
        mealPlan.setCreatedAt(LocalDateTime.now());
        mealPlan.setUpdatedAt(LocalDateTime.now());

        MealPlan savedPlan = mealPlanRepository.save(mealPlan);

        // Update daily routine with meal plan ID
        Optional<DailyRoutine> routineOpt = dailyRoutineRepository
                .findByUserIdAndDate(userId, targetDate);

        if (routineOpt.isPresent()) {
            DailyRoutine routine = routineOpt.get();
            routine.setMealPlanId(savedPlan.getId());
            routine.setUpdatedAt(LocalDateTime.now());
            dailyRoutineRepository.save(routine);
        }

        return savedPlan;
    }

    /**
     * Update progress for a routine
     */
    public DailyRoutine updateProgress(String userId, LocalDate date,
                                       DailyRoutine.DailyProgress progress) {
        logger.info("Updating progress for user: {} on date: {}", userId, date);

        DailyRoutine routine = dailyRoutineRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new RuntimeException("Routine not found"));

        routine.setProgress(progress);
        routine.setUpdatedAt(LocalDateTime.now());

        // Update status based on progress
        if (progress.getWorkoutCompletionPercentage() != null &&
                progress.getWorkoutCompletionPercentage() >= 100) {
            routine.setWorkoutCompleted(true);
        }

        if (progress.getMealAdherence() != null &&
                progress.getMealAdherence() >= 80) {
            routine.setMealPlanFollowed(true);
        }

        // Check if routine is completed
        if (routine.isWorkoutCompleted() && routine.isMealPlanFollowed()) {
            routine.setStatus("COMPLETED");
            routine.setCompletedAt(LocalDateTime.now());
        } else {
            routine.setStatus("IN_PROGRESS");
        }

        return dailyRoutineRepository.save(routine);
    }

    /**
     * Mark exercise as completed
     */
    public WorkoutPlan markExerciseCompleted(String workoutPlanId,
                                             int exerciseIndex,
                                             boolean completed) {
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout plan not found"));

        if (exerciseIndex >= 0 && exerciseIndex < plan.getExercises().size()) {
            plan.getExercises().get(exerciseIndex).setCompleted(completed);
            plan.setUpdatedAt(LocalDateTime.now());
        }

        return workoutPlanRepository.save(plan);
    }

    /**
     * Mark meal as consumed
     */
    public MealPlan markMealConsumed(String mealPlanId, int mealIndex, boolean consumed) {
        MealPlan plan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        if (mealIndex >= 0 && mealIndex < plan.getMeals().size()) {
            MealPlan.Meal meal = plan.getMeals().get(mealIndex);
            meal.setConsumed(consumed);
            if (consumed) {
                meal.setConsumedAt(LocalDateTime.now());
            }
            plan.setUpdatedAt(LocalDateTime.now());
        }

        return mealPlanRepository.save(plan);
    }

    /**
     * Create end-of-day summary
     */
    public DaySummary createDaySummary(String userId, LocalDate date) {
        logger.info("Creating day summary for user: {} on date: {}", userId, date);

        DailyRoutine routine = dailyRoutineRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new RuntimeException("Routine not found"));

        DaySummary summary = new DaySummary();
        summary.setUserId(userId);
        summary.setDate(date);
        summary.setDailyRoutineId(routine.getId());

        // Calculate overall performance
        DailyRoutine.DailyProgress progress = routine.getProgress();
        String performance = calculatePerformance(progress);
        summary.setOverallPerformance(performance);

        // Generate summary text
        String summaryText = generateSummaryText(routine, progress);
        summary.setSummary(summaryText);

        // Store metrics
        Map<String, Object> metrics = new HashMap<>();
        if (progress != null) {
            metrics.put("workoutCompletion", progress.getWorkoutCompletionPercentage());
            metrics.put("mealAdherence", progress.getMealAdherence());
            metrics.put("caloriesConsumed", progress.getCaloriesConsumed());
            metrics.put("caloriesBurned", progress.getCaloriesBurned());
            metrics.put("waterIntake", progress.getWaterIntake());
            metrics.put("sleepHours", progress.getSleepHours());
        }
        summary.setMetrics(metrics);

        // Identify achievements
        List<String> achievements = identifyAchievements(routine, progress);
        summary.setAchievements(achievements);

        // Identify improvements
        List<String> improvements = identifyImprovements(routine, progress);
        summary.setImprovements(improvements);

        DaySummary saved = daySummaryRepository.save(summary);

        // Update next day's routine with this summary
        LocalDate tomorrow = date.plusDays(1);
        Optional<DailyRoutine> tomorrowRoutine = dailyRoutineRepository
                .findByUserIdAndDate(userId, tomorrow);

        tomorrowRoutine.ifPresent(nextDay -> {
            nextDay.setPreviousDaySummaryId(saved.getId());
            dailyRoutineRepository.save(nextDay);
        });

        return saved;
    }

    /**
     * Get routine with all details (workout, meal, summary)
     */
    public Map<String, Object> getCompleteRoutine(String userId, LocalDate date) {
        Map<String, Object> complete = new HashMap<>();

        DailyRoutine routine = dailyRoutineRepository.findByUserIdAndDate(userId, date)
                .orElse(null);

        if (routine == null) {
            complete.put("exists", false);
            return complete;
        }

        complete.put("exists", true);
        complete.put("routine", routine);

        // Get workout plan
        if (routine.getWorkoutPlanId() != null) {
            workoutPlanRepository.findById(routine.getWorkoutPlanId())
                    .ifPresent(plan -> complete.put("workoutPlan", plan));
        }

        // Get meal plan
        if (routine.getMealPlanId() != null) {
            mealPlanRepository.findById(routine.getMealPlanId())
                    .ifPresent(plan -> complete.put("mealPlan", plan));
        }

        // Get previous day summary
        if (routine.getPreviousDaySummaryId() != null) {
            daySummaryRepository.findById(routine.getPreviousDaySummaryId())
                    .ifPresent(summary -> complete.put("previousDaySummary", summary));
        }

        return complete;
    }

    /**
     * Get weekly overview
     */
    public Map<String, Object> getWeeklyOverview(String userId, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);

        List<DailyRoutine> routines = dailyRoutineRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        Map<String, Object> overview = new HashMap<>();
        overview.put("startDate", startDate);
        overview.put("endDate", endDate);
        overview.put("routines", routines);

        // Calculate statistics
        long completedDays = routines.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .count();

        long workoutsCompleted = routines.stream()
                .filter(DailyRoutine::isWorkoutCompleted)
                .count();

        overview.put("completedDays", completedDays);
        overview.put("workoutsCompleted", workoutsCompleted);
        overview.put("completionRate", routines.isEmpty() ? 0 :
                (completedDays * 100.0 / routines.size()));

        return overview;
    }

    /**
     * Get progress trends
     */
    public Map<String, Object> getProgressTrends(String userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<DailyRoutine> routines = dailyRoutineRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        Map<String, Object> trends = new HashMap<>();
        trends.put("period", days + " days");
        trends.put("dataPoints", routines.size());

        // Extract trend data
        List<Map<String, Object>> dailyData = routines.stream()
                .map(routine -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("date", routine.getDate());
                    data.put("status", routine.getStatus());

                    DailyRoutine.DailyProgress progress = routine.getProgress();
                    if (progress != null) {
                        data.put("workoutCompletion", progress.getWorkoutCompletionPercentage());
                        data.put("mealAdherence", progress.getMealAdherence());
                        data.put("caloriesConsumed", progress.getCaloriesConsumed());
                        data.put("caloriesBurned", progress.getCaloriesBurned());
                    }

                    return data;
                })
                .collect(Collectors.toList());

        trends.put("dailyData", dailyData);

        return trends;
    }

    // Helper methods

    private String calculatePerformance(DailyRoutine.DailyProgress progress) {
        if (progress == null) return "AVERAGE";

        int score = 0;
        int factors = 0;

        if (progress.getWorkoutCompletionPercentage() != null) {
            score += progress.getWorkoutCompletionPercentage();
            factors++;
        }

        if (progress.getMealAdherence() != null) {
            score += progress.getMealAdherence();
            factors++;
        }

        if (factors == 0) return "AVERAGE";

        double average = score / (double) factors;

        if (average >= 90) return "EXCELLENT";
        if (average >= 75) return "GOOD";
        if (average >= 50) return "AVERAGE";
        return "POOR";
    }

    private String generateSummaryText(DailyRoutine routine,
                                       DailyRoutine.DailyProgress progress) {
        StringBuilder summary = new StringBuilder();
        summary.append("Day completed with ");
        summary.append(routine.getStatus().toLowerCase()).append(" status. ");

        if (progress != null) {
            if (progress.getWorkoutCompletionPercentage() != null) {
                summary.append("Workout: ")
                        .append(progress.getWorkoutCompletionPercentage())
                        .append("% completed. ");
            }

            if (progress.getMealAdherence() != null) {
                summary.append("Meal adherence: ")
                        .append(progress.getMealAdherence())
                        .append("%. ");
            }
        }

        return summary.toString();
    }

    private List<String> identifyAchievements(DailyRoutine routine,
                                              DailyRoutine.DailyProgress progress) {
        List<String> achievements = new ArrayList<>();

        if (routine.isWorkoutCompleted()) {
            achievements.add("Completed full workout");
        }

        if (progress != null) {
            if (progress.getMealAdherence() != null && progress.getMealAdherence() >= 90) {
                achievements.add("Excellent meal adherence");
            }

            if (progress.getWaterIntake() != null && progress.getWaterIntake() >= 2000) {
                achievements.add("Met daily water intake goal");
            }

            if (progress.getSleepHours() != null && progress.getSleepHours() >= 7) {
                achievements.add("Got adequate sleep");
            }
        }

        return achievements;
    }

    private List<String> identifyImprovements(DailyRoutine routine,
                                              DailyRoutine.DailyProgress progress) {
        List<String> improvements = new ArrayList<>();

        if (!routine.isWorkoutCompleted()) {
            improvements.add("Complete the full workout routine");
        }

        if (progress != null) {
            if (progress.getWaterIntake() != null && progress.getWaterIntake() < 2000) {
                improvements.add("Increase water intake");
            }

            if (progress.getSleepHours() != null && progress.getSleepHours() < 7) {
                improvements.add("Get more sleep");
            }
        }

        return improvements;
    }
}