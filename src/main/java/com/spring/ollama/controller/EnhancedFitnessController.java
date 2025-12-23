package com.spring.ollama.controller;

import com.spring.ollama.dto.CompleteFitnessPlanRequest;
import com.spring.ollama.dto.CompleteFitnessPlanResponse;
import com.spring.ollama.service.FitnessAiService;
import com.spring.ollama.service.StructuredFitnessPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Add these two new endpoints to your existing FitnessAiController
 */
@RestController
@RequestMapping("/api/fitness")
@CrossOrigin(origins = "http://localhost:3000")
public class EnhancedFitnessController {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedFitnessController.class);
    private final FitnessAiService fitnessAiService;

    private final StructuredFitnessPlanService structuredFitnessPlanService;

    public EnhancedFitnessController(FitnessAiService fitnessAiService,StructuredFitnessPlanService structuredFitnessPlanService) {
        this.fitnessAiService = fitnessAiService;
        this.structuredFitnessPlanService=structuredFitnessPlanService;
    }

    /**
     * NEW: Get complete plan with better parsing and return as structured JSON
     */
    @PostMapping(value = "/complete-plan-v2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCompleteFitnessPlanV2(@RequestBody CompleteFitnessPlanRequest request) {
        logger.info("Received COMPLETE fitness plan request (v2 - structured)");
        logger.debug("Request: {}", request);

        long startTime = System.currentTimeMillis();

        try {
            // Generate the complete plan with more structured prompt
            String completePlan = structuredFitnessPlanService.getCompleteFitnessPlanAsJson(
                    request.getGoal(),
                    request.getExperience(),
                    request.getDaysPerWeek(),
                    request.getTargetCalories(),
                    request.getDietaryPreference(),
                    request.getAge(),
                    request.getGender(),
                    request.getCurrentWeight(),
                    request.getTargetWeight(),
                    request.getHeight()
            );

            long endTime = System.currentTimeMillis();
            logger.info("Complete fitness plan (v2) generated in {} ms", (endTime - startTime));

            // Try to parse as JSON, if it fails return as text
            try {
                Object jsonResponse = new com.fasterxml.jackson.databind.ObjectMapper().readValue(completePlan, Object.class);
                return ResponseEntity.ok(jsonResponse);
            } catch (Exception e) {
                logger.warn("Response is not valid JSON, returning as text");
                return ResponseEntity.ok(completePlan);
            }

        } catch (Exception e) {
            logger.error("Error generating complete fitness plan (v2)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * NEW: Get complete plan - raw text format (better organized)
     */
    @PostMapping(value = "/complete-plan-text", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCompleteFitnessPlanText(@RequestBody CompleteFitnessPlanRequest request) {
        logger.info("Received COMPLETE fitness plan request (text format)");

        try {
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

            return ResponseEntity.ok(completePlan);

        } catch (Exception e) {
            logger.error("Error generating complete fitness plan (text)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
