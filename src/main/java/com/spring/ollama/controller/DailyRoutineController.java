//package com.spring.ollama.controller;
//
//import com.spring.ollama.entity.*;
//import com.spring.ollama.service.DailyRoutineService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/routine")
//@CrossOrigin(origins = "*")
//public class DailyRoutineController {
//
//    private static final Logger logger = LoggerFactory.getLogger(DailyRoutineController.class);
//
//    private final DailyRoutineService dailyRoutineService;
//
//    public DailyRoutineController(DailyRoutineService dailyRoutineService) {
//        this.dailyRoutineService = dailyRoutineService;
//    }
//
//    /**
//     * Get today's routine for current user
//     * GET /api/routine/today?userId=123
//     */
//    @GetMapping("/today")
//    public ResponseEntity<DailyRoutine> getTodayRoutine(@RequestParam String userId) {
//        logger.info("Getting today's routine for user: {}", userId);
//        try {
//            DailyRoutine routine = dailyRoutineService.getTodayRoutine(userId);
//            return ResponseEntity.ok(routine);
//        } catch (Exception e) {
//            logger.error("Error getting today's routine", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get complete routine with all details
//     * GET /api/routine/complete?userId=123&date=2025-12-25
//     */
//    @GetMapping("/complete")
//    public ResponseEntity<Map<String, Object>> getCompleteRoutine(
//            @RequestParam String userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//        logger.info("Getting complete routine for user: {} on date: {}", userId, date);
//        try {
//            Map<String, Object> routine = dailyRoutineService.getCompleteRoutine(userId, date);
//            return ResponseEntity.ok(routine);
//        } catch (Exception e) {
//            logger.error("Error getting complete routine", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Create workout plan
//     * POST /api/routine/workout
//     */
//    @PostMapping("/workout")
//    public ResponseEntity<Map<String, Object>> createWorkoutPlan(
//            @RequestParam String userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestBody WorkoutPlan workoutPlan) {
//
//        logger.info("Creating workout plan for user: {} on date: {}", userId, date);
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            WorkoutPlan saved = dailyRoutineService.createWorkoutPlan(userId, date, workoutPlan);
//            response.put("success", true);
//            response.put("workoutPlan", saved);
//            response.put("message", "Workout plan created successfully");
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//
//        } catch (Exception e) {
//            logger.error("Error creating workout plan", e);
//            response.put("success", false);
//            response.put("message", "Failed to create workout plan: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Create meal plan
//     * POST /api/routine/meal
//     */
//    @PostMapping("/meal")
//    public ResponseEntity<Map<String, Object>> createMealPlan(
//            @RequestParam String userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestBody MealPlan mealPlan) {
//
//        logger.info("Creating meal plan for user: {} on date: {}", userId, date);
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            MealPlan saved = dailyRoutineService.createMealPlan(userId, date, mealPlan);
//            response.put("success", true);
//            response.put("mealPlan", saved);
//            response.put("message", "Meal plan created successfully");
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//
//        } catch (Exception e) {
//            logger.error("Error creating meal plan", e);
//            response.put("success", false);
//            response.put("message", "Failed to create meal plan: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Update daily progress
//     * PUT /api/routine/progress
//     */
//    @PutMapping("/progress")
//    public ResponseEntity<Map<String, Object>> updateProgress(
//            @RequestParam String userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestBody DailyRoutine.DailyProgress progress) {
//
//        logger.info("Updating progress for user: {} on date: {}", userId, date);
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DailyRoutine updated = dailyRoutineService.updateProgress(userId, date, progress);
//            response.put("success", true);
//            response.put("routine", updated);
//            response.put("message", "Progress updated successfully");
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            logger.error("Error updating progress", e);
//            response.put("success", false);
//            response.put("message", "Failed to update progress: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Mark exercise as completed
//     * PUT /api/routine/workout/{workoutPlanId}/exercise/{index}/complete
//     */
//    @PutMapping("/workout/{workoutPlanId}/exercise/{index}/complete")
//    public ResponseEntity<Map<String, Object>> markExerciseCompleted(
//            @PathVariable String workoutPlanId,
//            @PathVariable int index,
//            @RequestParam boolean completed) {
//
//        logger.info("Marking exercise {} in workout {} as {}",
//                index, workoutPlanId, completed ? "completed" : "not completed");
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            WorkoutPlan updated = dailyRoutineService.markExerciseCompleted(
//                    workoutPlanId, index, completed);
//
//            response.put("success", true);
//            response.put("workoutPlan", updated);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            logger.error("Error marking exercise", e);
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Mark meal as consumed
//     * PUT /api/routine/meal/{mealPlanId}/meal/{index}/consume
//     */
//    @PutMapping("/meal/{mealPlanId}/meal/{index}/consume")
//    public ResponseEntity<Map<String, Object>> markMealConsumed(
//            @PathVariable String mealPlanId,
//            @PathVariable int index,
//            @RequestParam boolean consumed) {
//
//        logger.info("Marking meal {} in plan {} as {}",
//                index, mealPlanId, consumed ? "consumed" : "not consumed");
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            MealPlan updated = dailyRoutineService.markMealConsumed(
//                    mealPlanId, index, consumed);
//
//            response.put("success", true);
//            response.put("mealPlan", updated);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            logger.error("Error marking meal", e);
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Create end-of-day summary
//     * POST /api/routine/summary
//     */
//    @PostMapping("/summary")
//    public ResponseEntity<Map<String, Object>> createDaySummary(
//            @RequestParam String userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//        logger.info("Creating day summary for user: {} on date: {}", userId, date);
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DaySummary summary = dailyRoutineService.createDaySummary(userId, date);
//            response.put("success", true);
//            response.put("summary", summary);
//            response.put("message", "Day summary created successfully");
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//
//        } catch (Exception e) {
//            logger.error("Error creating summary", e);
//            response.put("success", false);
//            response.put("message", "Failed to create summary: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Get weekly overview
//     * GET /api/routine/weekly?userId=123&startDate=2025-12-20
//     */
//    @GetMapping("/weekly")
//    public ResponseEntity<Map<String, Object>> getWeeklyOverview(
//            @RequestParam String userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
//
//        logger.info("Getting weekly overview for user: {} starting: {}", userId, startDate);
//
//        try {
//            Map<String, Object> overview = dailyRoutineService.getWeeklyOverview(userId, startDate);
//            return ResponseEntity.ok(overview);
//        } catch (Exception e) {
//            logger.error("Error getting weekly overview", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get progress trends
//     * GET /api/routine/trends?userId=123&days=30
//     */
//    @GetMapping("/trends")
//    public ResponseEntity<Map<String, Object>> getProgressTrends(
//            @RequestParam String userId,
//            @RequestParam(defaultValue = "30") int days) {
//
//        logger.info("Getting progress trends for user: {} over {} days", userId, days);
//
//        try {
//            Map<String, Object> trends = dailyRoutineService.getProgressTrends(userId, days);
//            return ResponseEntity.ok(trends);
//        } catch (Exception e) {
//            logger.error("Error getting trends", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//}
