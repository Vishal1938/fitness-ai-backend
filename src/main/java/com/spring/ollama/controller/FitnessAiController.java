package com.spring.ollama.controller;


import com.spring.ollama.dto.CompleteFitnessPlanRequest;
import com.spring.ollama.dto.CompleteFitnessPlanResponse;
import com.spring.ollama.dto.ExerciseFormRequest;
import com.spring.ollama.dto.MealPlanRequest;
import com.spring.ollama.dto.PdfGenerationResponse;
import com.spring.ollama.dto.SupplementRequest;
import com.spring.ollama.dto.WorkoutPlanRequest;
import com.spring.ollama.entity.DailyRoutine;
import com.spring.ollama.service.DailyRoutineService;
import com.spring.ollama.service.FitnessAiService;
import com.spring.ollama.service.PdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/fitness")
@CrossOrigin(origins = "http://localhost:3000")
public class FitnessAiController {

    private static final Logger logger = LoggerFactory.getLogger(FitnessAiController.class);
    private final FitnessAiService fitnessAiService;
    private final PdfGeneratorService pdfGeneratorService;
    private final DailyRoutineService dailyRoutineService;

    public FitnessAiController(FitnessAiService fitnessAiService, PdfGeneratorService pdfGeneratorService,DailyRoutineService dailyRoutineService) {
        this.fitnessAiService = fitnessAiService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.dailyRoutineService=dailyRoutineService;
        logger.info("FitnessAiController initialized");
    }

    /**
     * General fitness question endpoint
     * Optional: Add conversationId parameter to maintain conversation history
     */
    @GetMapping("/ask")
    public ResponseEntity<String> askFitnessQuestion(
            @RequestParam String question,
            @RequestParam(required = false) String conversationId) {
        logger.info("Received fitness question at /api/fitness/ask");
        logger.debug("Question: {}, ConversationId: {}", question, conversationId);

        if (question == null || question.trim().isEmpty()) {
            logger.warn("Empty or null question received");
            return ResponseEntity.badRequest().body("Question parameter cannot be empty");
        }

        try {
            String response = fitnessAiService.askFitnessQuestion(question, conversationId);
            logger.info("Fitness question processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing fitness question: {}", question, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing your fitness question: " + e.getMessage());
        }
    }

    /**
     * Clear conversation history
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<String> clearConversation(@PathVariable String conversationId) {
        logger.info("Clearing conversation history for: {}", conversationId);
        try {
            fitnessAiService.clearConversationHistory(conversationId);
            return ResponseEntity.ok("Conversation history cleared successfully");
        } catch (Exception e) {
            logger.error("Error clearing conversation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error clearing conversation: " + e.getMessage());
        }
    }

    /**
     * Get personalized workout plan
     */
    @PostMapping("/workout-plan")
    public ResponseEntity<String> getWorkoutPlan(@RequestBody WorkoutPlanRequest request) {
        logger.info("Received workout plan request");
        logger.debug("Request: {}", request);

        try {
            String workoutPlan = fitnessAiService.getWorkoutPlan(
                    request.getGoal(),
                    request.getExperience(),
                    request.getDaysPerWeek()
            );
            logger.info("Workout plan generated successfully");
            return ResponseEntity.ok(workoutPlan);
        } catch (Exception e) {
            logger.error("Error generating workout plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating workout plan: " + e.getMessage());
        }
    }

    /**
     * Get personalized meal plan
     */
    @PostMapping("/meal-plan")
    public ResponseEntity<String> getMealPlan(@RequestBody MealPlanRequest request) {
        logger.info("Received meal plan request");
        logger.debug("Request: {}", request);

        try {
            String mealPlan = fitnessAiService.getMealPlan(
                    request.getGoal(),
                    request.getCalories(),
                    request.getDietaryPreference()
            );
            logger.info("Meal plan generated successfully");
            return ResponseEntity.ok(mealPlan);
        } catch (Exception e) {
            logger.error("Error generating meal plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating meal plan: " + e.getMessage());
        }
    }

    /**
     * Get exercise form tips
     */
    @PostMapping("/exercise-form")
    public ResponseEntity<String> getExerciseForm(@RequestBody ExerciseFormRequest request) {
        logger.info("Received exercise form request for: {}", request.getExerciseName());

        try {
            String formTips = fitnessAiService.getExerciseFormTips(request.getExerciseName());
            logger.info("Exercise form tips retrieved successfully");
            return ResponseEntity.ok(formTips);
        } catch (Exception e) {
            logger.error("Error getting exercise form tips", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting exercise form tips: " + e.getMessage());
        }
    }

    /**
     * Get supplement recommendations
     */
    @PostMapping("/supplements")
    public ResponseEntity<String> getSupplementAdvice(@RequestBody SupplementRequest request) {
        logger.info("Received supplement advice request");
        logger.debug("Request: {}", request);

        try {
            String advice = fitnessAiService.getSupplementAdvice(
                    request.getGoal(),
                    request.getDietType()
            );
            logger.info("Supplement advice generated successfully");
            return ResponseEntity.ok(advice);
        } catch (Exception e) {
            logger.error("Error getting supplement advice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting supplement advice: " + e.getMessage());
        }
    }

    /**
     * Generate a COMPLETE fitness plan (Workout + Meal + Supplements)
     * This is the all-in-one comprehensive plan endpoint
     */
    @PostMapping("/complete-plan")
    public ResponseEntity<CompleteFitnessPlanResponse> getCompleteFitnessPlan(
            @RequestBody CompleteFitnessPlanRequest request) {
        logger.info("Received COMPLETE fitness plan request");
        logger.debug("Request: {}", request);

        long startTime = System.currentTimeMillis();

        try {
            // Generate the complete plan
            String completePlan = fitnessAiService.getCompleteFitnessPlan(
                    request.getGoal(),
                    request.getExperience(),
                    request.getDaysPerWeek(),
                    request.getTargetCalories(),
                    request.getDietaryPreference(),
                    request.getAge(),
                    request.getGender(),
                    request.getCurrentWeight(),
                    request.getTargetWeight(),
                    request.getHeight(),
                    request.getAditionalInfo()
            );

            // Parse the response into sections (simple string split approach)
            CompleteFitnessPlanResponse response = parseCompletePlan(completePlan);

            long endTime = System.currentTimeMillis();
            response.setProcessingTimeMs(endTime - startTime);

            logger.info("Complete fitness plan generated successfully in {} ms", (endTime - startTime));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating complete fitness plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error generating complete fitness plan: " + e.getMessage()));
        }
    }


    @PostMapping("/user-complete-plan/{userId}/generate-pdf")
    public ResponseEntity<String> generateFitnessPlanPdf(@PathVariable String userId) {

        // 1. Generate structured journey synchronously
        String structuredJourney =
                fitnessAiService.generateStructuredFitnessPlan(userId);

        // 2. Fire async PDF + email
//        CompletableFuture.runAsync(() -> {
//            fitnessAiService.generatePdfAndSendEmail(userId, structuredJourney);
//        });

        // 3. Return structured journey immediately
//         Save to database
            DailyRoutine savedRoutine = dailyRoutineService.saveDailyRoutine(userId, structuredJourney);
            logger.info("Saved routine to database with id: {}", savedRoutine.getId());

            // Return the structured JSON to frontend
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(structuredJourney);
    }

    /**
     * Generate fitness plan and save to database
     */
//    @PostMapping("/user-complete-plan/{userId}/generate-pdf")
//    public ResponseEntity<String> generateFitnessPlanPdf(@PathVariable String userId) {
//        try {
//            logger.info("Generating fitness plan for user: {}", userId);
//
//            // Generate structured plan from LLM
//            String structuredPlan = fitnessAiService.generateStructuredPlanAndEmailAsync(userId);
//
//            // Save to database
//            DailyRoutine savedRoutine = dailyRoutineService.saveDailyRoutine(userId, structuredPlan);
//            logger.info("Saved routine to database with id: {}", savedRoutine.getId());
//
//            // Return the structured JSON to frontend
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(structuredPlan);
//
//        } catch (Exception e) {
//            logger.error("Error generating fitness plan for user: {}", userId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("{\"error\": \"Failed to generate fitness plan\"}");
//        }
//    }

    /**
     * Get all routines for a user
     */
    @GetMapping("/routines/{userId}")
    public ResponseEntity<List<DailyRoutine>> getAllRoutines(@PathVariable String userId) {
        try {
            List<DailyRoutine> routines = dailyRoutineService.getAllRoutinesByUser(userId);
            return ResponseEntity.ok(routines);
        } catch (Exception e) {
            logger.error("Error fetching routines for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific day's routine
     */
    @GetMapping("/routines/{userId}/day/{dayNumber}")
    public ResponseEntity<DailyRoutine> getRoutineByDay(
            @PathVariable String userId,
            @PathVariable int dayNumber) {
        try {
            Optional<DailyRoutine> routine = dailyRoutineService.getRoutineByDay(userId, dayNumber);
            return routine.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching routine for user: {}, day: {}", userId, dayNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get current (latest) routine
     */
    @GetMapping("/routines/{userId}/current")
    public ResponseEntity<String> getCurrentRoutine(@PathVariable String userId) {
        try {
            Optional<DailyRoutine> routine = dailyRoutineService.getCurrentRoutine(userId);
            if (routine.isPresent()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(routine.get().getStructuredPlanJson());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching current routine for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark a routine as completed
     */
    @PutMapping("/routines/{routineId}/complete")
    public ResponseEntity<DailyRoutine> markRoutineAsCompleted(@PathVariable String routineId) {
        try {
            DailyRoutine routine = dailyRoutineService.markRoutineAsCompleted(routineId);
            return ResponseEntity.ok(routine);
        } catch (Exception e) {
            logger.error("Error marking routine as completed: {}", routineId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete all routines for a user (reset)
     */
    @DeleteMapping("/routines/{userId}")
    public ResponseEntity<Void> deleteAllRoutines(@PathVariable String userId) {
        try {
            dailyRoutineService.deleteAllRoutinesForUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting routines for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/user-complete-plan/{userId}")
    public ResponseEntity<CompleteFitnessPlanResponse> getCompleteFitnessPlanForUser(
            @PathVariable String userId) {
        logger.info("Received COMPLETE fitness plan request");
        logger.debug("userId: {}", userId);

        long startTime = System.currentTimeMillis();

        try {
            // Generate the complete plan
            String completePlan = fitnessAiService.getCompleteFitnessPlan(
                    userId
            );

            // Parse the response into sections (simple string split approach)
            CompleteFitnessPlanResponse response = parseCompletePlan(completePlan);

            long endTime = System.currentTimeMillis();
            response.setProcessingTimeMs(endTime - startTime);

            logger.info("Complete fitness plan generated successfully in {} ms", (endTime - startTime));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating complete fitness plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error generating complete fitness plan: " + e.getMessage()));
        }
    }

    /**
     * Parse the complete plan response into structured sections
     */
    private CompleteFitnessPlanResponse parseCompletePlan(String completePlan) {
        CompleteFitnessPlanResponse response = new CompleteFitnessPlanResponse();

        // More robust parsing logic
        String workoutPlan = "";
        String mealPlan = "";
        String supplements = "";

        // Find workout plan section
        int workoutStart = findSectionStart(completePlan, "WORKOUT PLAN", "workout plan");
        int mealStart = findSectionStart(completePlan, "MEAL PLAN", "meal plan");
        int supplementStart = findSectionStart(completePlan, "SUPPLEMENT", "supplement");

        if (workoutStart != -1) {
            int workoutEnd = (mealStart != -1) ? mealStart :
                    (supplementStart != -1) ? supplementStart : completePlan.length();
            workoutPlan = completePlan.substring(workoutStart, workoutEnd).trim();
        }

        if (mealStart != -1) {
            int mealEnd = (supplementStart != -1) ? supplementStart : completePlan.length();
            mealPlan = completePlan.substring(mealStart, mealEnd).trim();
        }

        if (supplementStart != -1) {
            supplements = completePlan.substring(supplementStart).trim();
        }

        // Set the response
        response.setWorkoutPlan(workoutPlan.isEmpty() ? "Not found in response" : workoutPlan);
        response.setMealPlan(mealPlan.isEmpty() ? "Not found in response" : mealPlan);
        response.setSupplementRecommendations(supplements.isEmpty() ? "Not found in response" : supplements);
        response.setSummary("Complete fitness plan generated with " +
                (workoutPlan.isEmpty() ? "0" : "workout, ") +
                (mealPlan.isEmpty() ? "" : "meal, ") +
                (supplements.isEmpty() ? "" : "supplements"));

        return response;
    }

    /**
     * Helper method to find section start index
     */
    private int findSectionStart(String text, String... markers) {
        for (String marker : markers) {
            int index = text.indexOf(marker);
            if (index == -1) {
                index = text.toLowerCase().indexOf(marker.toLowerCase());
            }
            if (index != -1) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Create error response
     */
    private CompleteFitnessPlanResponse createErrorResponse(String errorMessage) {
        CompleteFitnessPlanResponse response = new CompleteFitnessPlanResponse();
        response.setSummary(errorMessage);
        response.setWorkoutPlan("Error occurred");
        response.setMealPlan("Error occurred");
        response.setSupplementRecommendations("Error occurred");
        return response;
    }

    /**
     * Generate a COMPLETE fitness plan (Workout + Meal + Supplements) and save as PDF
     */
    @PostMapping("/complete-plan-pdf")
    public ResponseEntity<PdfGenerationResponse> getCompleteFitnessPlanAsPdf(
            @RequestBody CompleteFitnessPlanRequest request,
            @RequestParam(required = false) String fileName) {
        logger.info("Received COMPLETE fitness plan PDF request");
        logger.debug("Request: {}", request);

        long startTime = System.currentTimeMillis();

        try {
            // Generate the complete plan
            String completePlan = fitnessAiService.getCompleteFitnessPlan(
                    request.getGoal(),
                    request.getExperience(),
                    request.getDaysPerWeek(),
                    request.getTargetCalories(),
                    request.getDietaryPreference(),
                    request.getAge(),
                    request.getGender(),
                    request.getCurrentWeight(),
                    request.getTargetWeight(),
                    request.getHeight(),
                    request.getAditionalInfo()
            );

            // Generate filename if not provided
            if (fileName == null || fileName.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                fileName = "fitness_plan_" + request.getGoal().replaceAll("\\s+", "_") + "_" + timestamp;
            }

            // Generate PDF
            String pdfPath = pdfGeneratorService.generateFitnessPlanPdf(completePlan, fileName);

            // Get file info
            File pdfFile = new File(pdfPath);
            long fileSize = pdfFile.length();

            long endTime = System.currentTimeMillis();

            // Create response
            PdfGenerationResponse response = new PdfGenerationResponse(
                    true,
                    "Fitness plan PDF generated successfully",
                    pdfPath,
                    pdfFile.getName()
            );
            response.setFileSizeBytes(fileSize);
            response.setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            logger.info("Complete fitness plan PDF generated successfully in {} ms at: {}",
                    (endTime - startTime), pdfPath);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating complete fitness plan PDF", e);
            PdfGenerationResponse errorResponse = new PdfGenerationResponse(
                    false,
                    "Error generating PDF: " + e.getMessage(),
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generate workout plan and save as PDF
     */
    @PostMapping("/workout-plan-pdf")
    public ResponseEntity<PdfGenerationResponse> getWorkoutPlanAsPdf(
            @RequestBody WorkoutPlanRequest request,
            @RequestParam(required = false) String fileName) {
        logger.info("Received workout plan PDF request");

        try {
            String workoutPlan = fitnessAiService.getWorkoutPlan(
                    request.getGoal(),
                    request.getExperience(),
                    request.getDaysPerWeek()
            );

            if (fileName == null || fileName.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                fileName = "workout_plan_" + timestamp;
            }

            String pdfPath = pdfGeneratorService.generateFitnessPlanPdf(workoutPlan, fileName);
            File pdfFile = new File(pdfPath);

            PdfGenerationResponse response = new PdfGenerationResponse(
                    true,
                    "Workout plan PDF generated successfully",
                    pdfPath,
                    pdfFile.getName()
            );
            response.setFileSizeBytes(pdfFile.length());
            response.setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            logger.info("Workout plan PDF generated at: {}", pdfPath);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating workout plan PDF", e);
            PdfGenerationResponse errorResponse = new PdfGenerationResponse(
                    false,
                    "Error generating PDF: " + e.getMessage(),
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generate meal plan and save as PDF
     */
    @PostMapping("/meal-plan-pdf")
    public ResponseEntity<PdfGenerationResponse> getMealPlanAsPdf(
            @RequestBody MealPlanRequest request,
            @RequestParam(required = false) String fileName) {
        logger.info("Received meal plan PDF request");

        try {
            String mealPlan = fitnessAiService.getMealPlan(
                    request.getGoal(),
                    request.getCalories(),
                    request.getDietaryPreference()
            );

            if (fileName == null || fileName.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                fileName = "meal_plan_" + timestamp;
            }

            String pdfPath = pdfGeneratorService.generateFitnessPlanPdf(mealPlan, fileName);
            File pdfFile = new File(pdfPath);

            PdfGenerationResponse response = new PdfGenerationResponse(
                    true,
                    "Meal plan PDF generated successfully",
                    pdfPath,
                    pdfFile.getName()
            );
            response.setFileSizeBytes(pdfFile.length());
            response.setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            logger.info("Meal plan PDF generated at: {}", pdfPath);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating meal plan PDF", e);
            PdfGenerationResponse errorResponse = new PdfGenerationResponse(
                    false,
                    "Error generating PDF: " + e.getMessage(),
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Fitness AI Service is running!");
    }
}