package com.spring.ollama.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FitnessPlanParser {

    private static final Logger logger = LoggerFactory.getLogger(FitnessPlanParser.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse LLM fitness plan response into structured JSON format
     */
    public String parseToStructuredJourney(String llmResponse, int currentDay) {
        try {
            // Create the root journey object
            ObjectNode journey = objectMapper.createObjectNode();
            journey.put("currentDay", currentDay);
            journey.put("totalDays", 7);

            // Add overview
            ObjectNode overview = objectMapper.createObjectNode();
            overview.put("weeklyGoal", "Build strength and improve endurance");
            overview.put("estimatedProgress", "Expected to see initial improvements in energy and strength");
            journey.set("overview", overview);

            // Parse the LLM response for today's plan
            ObjectNode dayPlan = parseSingleDayPlan(llmResponse, currentDay);

            // Create days array with just today's plan
            ArrayNode daysArray = objectMapper.createArrayNode();
            daysArray.add(dayPlan);
            journey.set("days", daysArray);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(journey);

        } catch (Exception e) {
            logger.error("Error parsing fitness plan", e);
            return getFallbackJourney(currentDay);
        }
    }

    /**
     * Parse a single day's plan from LLM response
     */
    private ObjectNode parseSingleDayPlan(String llmResponse, int day) {
        ObjectNode dayPlan = objectMapper.createObjectNode();
        dayPlan.put("day", day);

        // Parse workout section
        ObjectNode workout = parseWorkoutSection(llmResponse);
        dayPlan.set("workout", workout);

        // Parse meal section
        ObjectNode meal = parseMealSection(llmResponse);
        dayPlan.set("meal", meal);

        // Extract tips
        String tips = extractTips(llmResponse);
        dayPlan.put("tips", tips);

        return dayPlan;
    }

    /**
     * Parse workout section from LLM response
     */
    private ObjectNode parseWorkoutSection(String response) {
        ObjectNode workout = objectMapper.createObjectNode();

        try {
            // Extract workout title
            String title = extractSection(response,
                    "(?i)(workout plan|today'?s workout|exercise plan)[:\\s]*([^\n]+)", 2);
            workout.put("title", title != null ? title.trim() : "Full Body Workout");

            // Extract duration
            String duration = extractDuration(response);
            workout.put("duration", duration);

            // Extract exercises
            List<String> exercises = extractExercises(response);
            ArrayNode exercisesArray = objectMapper.createArrayNode();
            exercises.forEach(exercisesArray::add);
            workout.set("exercises", exercisesArray);

            // Extract notes
            String notes = extractWorkoutNotes(response);
            workout.put("notes", notes);

        } catch (Exception e) {
            logger.error("Error parsing workout section", e);
            setDefaultWorkout(workout);
        }

        return workout;
    }

    /**
     * Parse meal section from LLM response
     */
    private ObjectNode parseMealSection(String response) {
        ObjectNode meal = objectMapper.createObjectNode();

        try {
            // Extract meals
            meal.put("breakfast", extractMeal(response, "breakfast"));
            meal.put("lunch", extractMeal(response, "lunch"));
            meal.put("dinner", extractMeal(response, "dinner"));
            meal.put("snacks", extractMeal(response, "snack"));

            // Extract calories and macros
            String totalCalories = extractCalories(response);
            meal.put("totalCalories", totalCalories);

            String macros = extractMacros(response);
            meal.put("macros", macros);

        } catch (Exception e) {
            logger.error("Error parsing meal section", e);
            setDefaultMeals(meal);
        }

        return meal;
    }

    /**
     * Extract workout duration from response
     */
    private String extractDuration(String response) {
        Pattern pattern = Pattern.compile("(?i)(\\d+)\\s*(?:to|-)\\s*(\\d+)?\\s*min(?:ute)?s?");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            if (matcher.group(2) != null) {
                return matcher.group(1) + "-" + matcher.group(2) + " minutes";
            }
            return matcher.group(1) + " minutes";
        }

        return "45 minutes";
    }

    /**
     * Extract exercises list from response
     */
    private List<String> extractExercises(String response) {
        List<String> exercises = new ArrayList<>();

        // Find workout section
        Pattern sectionPattern = Pattern.compile(
                "(?i)(?:workout|exercise)\\s*plan.*?(?=(?:meal|diet|supplement|$))",
                Pattern.DOTALL
        );
        Matcher sectionMatcher = sectionPattern.matcher(response);

        String workoutSection = "";
        if (sectionMatcher.find()) {
            workoutSection = sectionMatcher.group();
        }

        // Extract bullet points or numbered items
        Pattern exercisePattern = Pattern.compile(
                "(?:^|\\n)\\s*(?:[•\\-*]|\\d+[\\.\\)]?)\\s*([^\n]+)",
                Pattern.MULTILINE
        );
        Matcher exerciseMatcher = exercisePattern.matcher(workoutSection);

        while (exerciseMatcher.find()) {
            String exercise = exerciseMatcher.group(1).trim();
            if (!exercise.isEmpty() && exercise.length() > 3) {
                exercises.add(exercise);
            }
        }

        // If no exercises found, add default
        if (exercises.isEmpty()) {
            exercises.add("Warm-up: 5 min light cardio");
            exercises.add("Strength training exercises");
            exercises.add("Cool-down stretches");
        }

        return exercises;
    }

    /**
     * Extract meal for a specific meal type
     */
    private String extractMeal(String response, String mealType) {
        Pattern pattern = Pattern.compile(
                "(?i)" + mealType + "\\s*:?\\s*([^\n]+(?:\n(?!(?:breakfast|lunch|dinner|snack):)[^\n]+)*)",
                Pattern.MULTILINE
        );
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            String meal = matcher.group(1).trim();
            // Clean up the meal text
            meal = meal.replaceAll("\\s+", " ");
            return meal;
        }

        return "Balanced meal with protein, carbs, and healthy fats";
    }

    /**
     * Extract total calories from response
     */
    private String extractCalories(String response) {
        Pattern pattern = Pattern.compile("(?i)(\\d{3,4})\\s*(?:total)?\\s*cal(?:ories)?");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1) + " cal";
        }

        return "1800 cal";
    }

    /**
     * Extract macros from response
     */
    private String extractMacros(String response) {
        Pattern proteinPattern = Pattern.compile("(?i)protein[:\\s]*(\\d+)\\s*g");
        Pattern carbsPattern = Pattern.compile("(?i)carb(?:ohydrate)?s?[:\\s]*(\\d+)\\s*g");
        Pattern fatsPattern = Pattern.compile("(?i)fats?[:\\s]*(\\d+)\\s*g");

        String protein = "120g";
        String carbs = "180g";
        String fats = "60g";

        Matcher proteinMatcher = proteinPattern.matcher(response);
        if (proteinMatcher.find()) {
            protein = proteinMatcher.group(1) + "g";
        }

        Matcher carbsMatcher = carbsPattern.matcher(response);
        if (carbsMatcher.find()) {
            carbs = carbsMatcher.group(1) + "g";
        }

        Matcher fatsMatcher = fatsPattern.matcher(response);
        if (fatsMatcher.find()) {
            fats = fatsMatcher.group(1) + "g";
        }

        return "Protein: " + protein + " | Carbs: " + carbs + " | Fats: " + fats;
    }

    /**
     * Extract workout notes
     */
    private String extractWorkoutNotes(String response) {
        Pattern pattern = Pattern.compile(
                "(?i)(?:note|tip|important|remember)[:\\s]*([^\n]+)",
                Pattern.MULTILINE
        );
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "Focus on proper form and listen to your body.";
    }

    /**
     * Extract tips from response
     */
    private String extractTips(String response) {
        Pattern pattern = Pattern.compile(
                "(?i)(?:tip|advice|recommendation)[:\\s]*([^\n]+)",
                Pattern.MULTILINE
        );
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "Stay hydrated! Aim for at least 8 glasses of water today.";
    }

    /**
     * Extract a section from response using regex
     */
    private String extractSection(String response, String regex, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(group);
        }

        return null;
    }

    /**
     * Set default workout values
     */
    private void setDefaultWorkout(ObjectNode workout) {
        workout.put("title", "Full Body Workout");
        workout.put("duration", "45 minutes");

        ArrayNode exercises = objectMapper.createArrayNode();
        exercises.add("Warm-up: 5 min light cardio");
        exercises.add("Squats: 3 sets x 12 reps");
        exercises.add("Push-ups: 3 sets x 10 reps");
        exercises.add("Plank: 3 sets x 30 seconds");
        workout.set("exercises", exercises);

        workout.put("notes", "Focus on proper form.");
    }

    /**
     * Set default meal values
     */
    private void setDefaultMeals(ObjectNode meal) {
        meal.put("breakfast", "Oatmeal with berries and almonds (350 cal)");
        meal.put("lunch", "Grilled chicken salad with quinoa (450 cal)");
        meal.put("dinner", "Baked salmon with sweet potato and broccoli (500 cal)");
        meal.put("snacks", "Greek yogurt, apple, protein shake");
        meal.put("totalCalories", "1800 cal");
        meal.put("macros", "Protein: 120g | Carbs: 180g | Fats: 60g");
    }

    /**
     * Fallback journey if parsing fails
     */
    private String getFallbackJourney(int currentDay) {
        try {
            ObjectNode journey = objectMapper.createObjectNode();
            journey.put("currentDay", currentDay);
            journey.put("totalDays", 7);

            ObjectNode overview = objectMapper.createObjectNode();
            overview.put("weeklyGoal", "Build strength and improve endurance");
            overview.put("estimatedProgress", "Expected to see initial improvements");
            journey.set("overview", overview);

            ObjectNode dayPlan = objectMapper.createObjectNode();
            dayPlan.put("day", currentDay);

            ObjectNode workout = objectMapper.createObjectNode();
            setDefaultWorkout(workout);
            dayPlan.set("workout", workout);

            ObjectNode meal = objectMapper.createObjectNode();
            setDefaultMeals(meal);
            dayPlan.set("meal", meal);

            dayPlan.put("tips", "Stay hydrated and maintain consistency!");

            ArrayNode daysArray = objectMapper.createArrayNode();
            daysArray.add(dayPlan);
            journey.set("days", daysArray);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(journey);

        } catch (Exception e) {
            logger.error("Error creating fallback journey", e);
            return "{}";
        }
    }
}