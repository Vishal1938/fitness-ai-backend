package com.spring.ollama.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Alternative method to get structured JSON response for complete fitness plan
 * Add this method to your existing FitnessAiService class
 */
@Service
public class StructuredFitnessPlanService {

    private static final Logger logger = LoggerFactory.getLogger(StructuredFitnessPlanService.class);
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public StructuredFitnessPlanService(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
        logger.info("StructuredFitnessPlanService initialized");
    }

    /**
     * Generate complete fitness plan with structured JSON response
     */
    public String getCompleteFitnessPlanAsJson(String goal, String experience, String daysPerWeek,
                                               String targetCalories, String dietaryPreference,
                                               String age, String gender, String currentWeight,
                                               String targetWeight, String height) {
        logger.info("Generating STRUCTURED JSON fitness plan - Goal: {}, Experience: {}, Days/Week: {}",
                goal, experience, daysPerWeek);

        // Build a prompt that explicitly requests JSON format
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Create a complete fitness plan and return ONLY a valid JSON object with the following structure:\n\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"workoutPlan\": {\n");
        promptBuilder.append("    \"days\": [\n");
        promptBuilder.append("      {\n");
        promptBuilder.append("        \"day\": \"Day 1\",\n");
        promptBuilder.append("        \"focus\": \"Chest and Triceps\",\n");
        promptBuilder.append("        \"exercises\": [\n");
        promptBuilder.append("          {\"name\": \"Bench Press\", \"sets\": 3, \"reps\": \"8-12\", \"rest\": \"90s\"}\n");
        promptBuilder.append("        ]\n");
        promptBuilder.append("      }\n");
        promptBuilder.append("    ],\n");
        promptBuilder.append("    \"progressionStrategy\": \"description here\"\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"mealPlan\": {\n");
        promptBuilder.append("    \"meals\": [\n");
        promptBuilder.append("      {\n");
        promptBuilder.append("        \"meal\": \"Breakfast\",\n");
        promptBuilder.append("        \"time\": \"7:00 AM\",\n");
        promptBuilder.append("        \"calories\": 650,\n");
        promptBuilder.append("        \"items\": [{\"food\": \"Oatmeal\", \"amount\": \"80g\"}],\n");
        promptBuilder.append("        \"macros\": {\"protein\": 30, \"carbs\": 70, \"fats\": 15}\n");
        promptBuilder.append("      }\n");
        promptBuilder.append("    ],\n");
        promptBuilder.append("    \"mealPrepTips\": [\"tip1\", \"tip2\"]\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"supplements\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"name\": \"Whey Protein\",\n");
        promptBuilder.append("      \"dosage\": \"25-30g\",\n");
        promptBuilder.append("      \"timing\": \"Post-workout\",\n");
        promptBuilder.append("      \"reason\": \"Fast-absorbing protein\"\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n\n");

        promptBuilder.append("User Details:\n");
        promptBuilder.append("- Goal: ").append(goal).append("\n");
        promptBuilder.append("- Experience: ").append(experience).append("\n");
        promptBuilder.append("- Training Days: ").append(daysPerWeek).append(" days/week\n");
        promptBuilder.append("- Target Calories: ").append(targetCalories).append("\n");
        promptBuilder.append("- Diet: ").append(dietaryPreference).append("\n");
        if (age != null) promptBuilder.append("- Age: ").append(age).append("\n");
        if (gender != null) promptBuilder.append("- Gender: ").append(gender).append("\n");
        if (currentWeight != null) promptBuilder.append("- Current Weight: ").append(currentWeight).append("\n");
        if (targetWeight != null) promptBuilder.append("- Target Weight: ").append(targetWeight).append("\n");
        if (height != null) promptBuilder.append("- Height: ").append(height).append("\n");

        promptBuilder.append("\nIMPORTANT: Return ONLY the JSON object. Do not include any markdown formatting, ");
        promptBuilder.append("code blocks, or explanatory text. Just pure JSON that can be parsed directly.");

        String prompt = promptBuilder.toString();
        logger.debug("Structured JSON prompt length: {} characters", prompt.length());

        try {
            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .content();

            // Clean the response - remove markdown code blocks if present
            String cleanedResponse = cleanJsonResponse(response);

            // Validate it's valid JSON
            try {
                objectMapper.readTree(cleanedResponse);
                logger.info("Successfully generated valid JSON fitness plan");
                return cleanedResponse;
            } catch (Exception e) {
                logger.warn("Response is not valid JSON, returning raw response");
                return response;
            }

        } catch (Exception e) {
            logger.error("Error generating structured fitness plan", e);
            throw new RuntimeException("Failed to generate structured fitness plan", e);
        }
    }

    /**
     * Clean JSON response by removing markdown code blocks
     */
    private String cleanJsonResponse(String response) {
        // Remove markdown JSON code blocks
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }
}