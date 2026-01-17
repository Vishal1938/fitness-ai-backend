package com.spring.ollama.service;

import com.spring.ollama.dto.CompleteFitnessPlanRequest;
import com.spring.ollama.dto.ScheduledReportRequest;
import com.spring.ollama.entity.User;
import com.spring.ollama.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FitnessAiService {

    private static final Logger logger = LoggerFactory.getLogger(FitnessAiService.class);
    private final ChatClient chatClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private EmailService emailService;

    // In-memory conversation history storage
    private final Map<String, List<ConversationMessage>> conversationHistory = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY_SIZE = 10;

    // Keywords for fitness domain validation
    private static final String[] FITNESS_KEYWORDS = {
            "workout", "exercise", "gym", "fitness", "training", "muscle", "cardio",
            "nutrition", "diet", "protein", "calories", "weight", "fat", "supplement",
            "yoga", "strength", "running", "bodybuilding", "health", "meal", "food",
            "macros", "vitamins", "recovery", "stretching", "flexibility", "endurance"
    };

    public FitnessAiService(ChatClient chatClient) {
        this.chatClient = chatClient;
        logger.info("FitnessAiService initialized with domain-restricted ChatClient");
    }

    /**
     * Main method to ask fitness-related questions (without conversation memory)
     */
    public String askFitnessQuestion(String question) {
        return askFitnessQuestion(question, null);
    }

    /**
     * Ask fitness question with optional conversation ID for memory
     */
    public String askFitnessQuestion(String question, String conversationId) {
        logger.info("Received fitness question: {}", question);
        long startTime = System.currentTimeMillis();

        try {
            // Pre-validation (optional - the system prompt will handle this too)
            if (!isLikelyFitnessRelated(question)) {
                logger.warn("Question may not be fitness-related: {}", question);
            }

            // Add conversation context if conversationId is provided
            String enhancedQuestion = question;
            if (conversationId != null && !conversationId.trim().isEmpty()) {
                String context = getConversationContext(conversationId);
                if (!context.isEmpty()) {
                    enhancedQuestion = context + "\n\nCurrent question: " + question;
                    logger.debug("Added conversation context for conversationId: {}", conversationId);
                }
            }

            logger.debug("Sending prompt to Fitness ChatClient");
            String response = chatClient
                    .prompt(enhancedQuestion)
                    .call()
                    .content();

            // Store in conversation memory if conversationId provided
            if (conversationId != null && !conversationId.trim().isEmpty()) {
                addMessageToHistory(conversationId, "user", question);
                addMessageToHistory(conversationId, "assistant", response);
                logger.debug("Stored conversation in memory for conversationId: {}", conversationId);
            }

            long endTime = System.currentTimeMillis();
            logger.info("Successfully processed fitness question in {} ms. Response length: {} characters",
                    (endTime - startTime), response.length());
            logger.debug("Response: {}", response);

            return response;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("Error processing fitness question after {} ms: {}",
                    (endTime - startTime), question, e);
            throw new RuntimeException("Failed to get AI response for fitness query", e);
        }
    }

    /**
     * Get a personalized workout plan
     */
    public String getWorkoutPlan(String goal, String experience, String daysPerWeek) {
        logger.info("Generating workout plan - Goal: {}, Experience: {}, Days/Week: {}",
                goal, experience, daysPerWeek);

        String promptText = """
                Create a detailed workout plan with the following specifications:
                - Goal: {goal}
                - Experience Level: {experience}
                - Days per week: {daysPerWeek}
                
                Please include:
                1. Weekly schedule with specific exercises
                2. Sets and reps for each exercise
                3. Rest periods
                4. Progression tips
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
                "goal", goal,
                "experience", experience,
                "daysPerWeek", daysPerWeek
        ));

        return chatClient.prompt(prompt).call().content();
    }

    /**
     * Get a meal plan based on dietary requirements
     */
    public String getMealPlan(String goal, String calories, String dietaryPreference) {
        logger.info("Generating meal plan - Goal: {}, Calories: {}, Diet: {}",
                goal, calories, dietaryPreference);

        String promptText = """
                Create a daily meal plan with these requirements:
                - Goal: {goal}
                - Target Calories: {calories}
                - Dietary Preference: {dietaryPreference}
                
                Please provide:
                1. Breakfast, lunch, dinner, and 2 snacks
                2. Macronutrient breakdown for each meal
                3. Portion sizes
                4. Meal prep tips
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
                "goal", goal,
                "calories", calories,
                "dietaryPreference", dietaryPreference
        ));

        return chatClient.prompt(prompt).call().content();
    }

    /**
     * Get exercise form tips
     */
    public String getExerciseFormTips(String exerciseName) {
        logger.info("Fetching form tips for exercise: {}", exerciseName);

        String question = String.format(
                "Provide detailed form tips and common mistakes to avoid for the exercise: %s. " +
                        "Include muscle groups worked and safety considerations.",
                exerciseName
        );

        return askFitnessQuestion(question);
    }

    /**
     * Get supplement recommendations
     */
    public String getSupplementAdvice(String goal, String dietType) {
        logger.info("Getting supplement advice - Goal: {}, Diet: {}", goal, dietType);

        String question = String.format(
                "What supplements would you recommend for someone with a goal of %s " +
                        "following a %s diet? Include dosage recommendations and timing.",
                goal, dietType
        );

        return askFitnessQuestion(question);
    }

    /**
     * Generate a complete fitness plan including workout, meal plan, and supplements
     * This is a comprehensive plan generation in one call
     */
    public String getCompleteFitnessPlan(String goal, String experience, String daysPerWeek,
                                         String targetCalories, String dietaryPreference,
                                         String age, String gender, String currentWeight,
                                         String targetWeight, String height,String aditionalInfo) {
        logger.info("Generating COMPLETE fitness plan - Goal: {}, Experience: {}, Days/Week: {}",
                goal, experience, daysPerWeek);

        // Build a comprehensive prompt that requests all three components
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Create a COMPLETE and COMPREHENSIVE fitness plan for me with the following details:\n\n");

        // Personal Information
        promptBuilder.append("=== PERSONAL INFORMATION ===\n");
        promptBuilder.append("Goal: ").append(goal).append("\n");
        promptBuilder.append("Experience Level: ").append(experience).append("\n");
        if (age != null) promptBuilder.append("Age: ").append(age).append("\n");
        if (gender != null) promptBuilder.append("Gender: ").append(gender).append("\n");
        if (currentWeight != null) promptBuilder.append("Current Weight: ").append(currentWeight).append("\n");
        if (targetWeight != null) promptBuilder.append("Target Weight: ").append(targetWeight).append("\n");
        if (height != null) promptBuilder.append("Height: ").append(height).append("\n");
        if(aditionalInfo!=null)promptBuilder.append("Note: ").append(aditionalInfo).append("\n");

        promptBuilder.append("\n=== REQUIREMENTS ===\n");
        promptBuilder.append("Training Days per Week: ").append(daysPerWeek).append("\n");
        promptBuilder.append("Target Daily Calories: ").append(targetCalories).append("\n");
        promptBuilder.append("Dietary Preference: ").append(dietaryPreference).append("\n");

        promptBuilder.append("\n\nPlease provide a DETAILED plan with the following THREE sections:\n\n");

        promptBuilder.append("1. WORKOUT PLAN:\n");
        promptBuilder.append("   - Complete weekly schedule (Day 1, Day 2, etc.)\n");
        promptBuilder.append("   - Specific exercises for each day\n");
        promptBuilder.append("   - Sets, reps, and rest periods\n");
        promptBuilder.append("   - Warm-up and cool-down routines\n");
        promptBuilder.append("   - Progression strategy\n\n");

        promptBuilder.append("2. MEAL PLAN:\n");
        promptBuilder.append("   - Daily meal breakdown (Breakfast, Lunch, Dinner, Snacks)\n");
        promptBuilder.append("   - Specific food items and portion sizes\n");
        promptBuilder.append("   - Macronutrient breakdown (Protein, Carbs, Fats)\n");
        promptBuilder.append("   - Meal timing recommendations\n");
        promptBuilder.append("   - Meal prep tips\n\n");

        promptBuilder.append("3. SUPPLEMENT RECOMMENDATIONS:\n");
        promptBuilder.append("   - Essential supplements for my goal\n");
        promptBuilder.append("   - Dosage recommendations\n");
        promptBuilder.append("   - Timing (when to take each supplement)\n");
        promptBuilder.append("   - Why each supplement is recommended\n\n");

        promptBuilder.append("Make the plan practical, sustainable, and aligned with my goal of ").append(goal).append(".");

        String prompt = promptBuilder.toString();
        logger.debug("Complete plan prompt length: {} characters", prompt.length());

        return askFitnessQuestion(prompt);
    }


    public String getCompleteFitnessPlan(String userId) {
       Optional<User> optionalUser =userRepository.findById(userId);
       User loggedInuser=null;
       if(optionalUser.isPresent()){
           loggedInuser =optionalUser.get();
       }

        logger.info("Generating COMPLETE fitness plan for user -name :{} ,age :{}, Goal: {},currentWeight: {},TargetWeight: {}, Experience: {}, Height: {}",
                loggedInuser.getFirstName(),loggedInuser.getAge(), loggedInuser.getFitnessGoal(), loggedInuser.getCurrentWeight(),loggedInuser.getTargetWeight(),loggedInuser.getExperienceLevel(),loggedInuser.getHeight());

        // Build a comprehensive prompt that requests all three components
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Create a COMPLETE and COMPREHENSIVE fitness plan for me with the following details:\n\n");

        // Personal Information
        promptBuilder.append("=== PERSONAL INFORMATION ===\n");
        promptBuilder.append("Goal: ").append(loggedInuser.getFitnessGoal()).append("\n");
        promptBuilder.append("Experience Level: ").append(loggedInuser.getExperienceLevel()).append("\n");
        if (loggedInuser.getAge() != null) promptBuilder.append("Age: ").append(loggedInuser.getAge()).append("\n");
        if (loggedInuser.getGender() != null) promptBuilder.append("Gender: ").append(loggedInuser.getGender()).append("\n");
        if (loggedInuser.getCurrentWeight() != null) promptBuilder.append("Current Weight: ").append(loggedInuser.getCurrentWeight()).append("\n");
        if (loggedInuser.getTargetWeight() != null) promptBuilder.append("Target Weight: ").append(loggedInuser.getTargetWeight()).append("\n");
        if (loggedInuser.getHeight() != null) promptBuilder.append("Height: ").append(loggedInuser.getHeight()).append("\n");
//        if(aditionalInfo!=null)promptBuilder.append("Note: ").append(aditionalInfo).append("\n");

        promptBuilder.append("\n=== REQUIREMENTS ===\n");
        promptBuilder.append("Training Days per Week: ").append("7").append("\n");
        promptBuilder.append("Target Daily Calories: ").append("2000").append("\n");
        promptBuilder.append("Dietary Preference: ").append(loggedInuser.getDietaryPreference()).append("\n");

        promptBuilder.append("\n\nPlease provide a DETAILED plan with the following THREE sections:\n\n");

        promptBuilder.append("1. WORKOUT PLAN:\n");
        promptBuilder.append("   - Complete weekly schedule (Day 1, Day 2, etc.)\n");
        promptBuilder.append("   - Specific exercises for each day\n");
        promptBuilder.append("   - Sets, reps, and rest periods\n");
        promptBuilder.append("   - Warm-up and cool-down routines\n");
        promptBuilder.append("   - Progression strategy\n\n");

        promptBuilder.append("2. MEAL PLAN:\n");
        promptBuilder.append("   - Daily meal breakdown (Breakfast, Lunch, Dinner, Snacks)\n");
        promptBuilder.append("   - Specific food items and portion sizes\n");
        promptBuilder.append("   - Macronutrient breakdown (Protein, Carbs, Fats)\n");
        promptBuilder.append("   - Meal timing recommendations\n");
        promptBuilder.append("   - Meal prep tips\n\n");

        promptBuilder.append("3. SUPPLEMENT RECOMMENDATIONS:\n");
        promptBuilder.append("   - Essential supplements for my goal\n");
        promptBuilder.append("   - Dosage recommendations\n");
        promptBuilder.append("   - Timing (when to take each supplement)\n");
        promptBuilder.append("   - Why each supplement is recommended\n\n");

        promptBuilder.append("Make the plan practical, sustainable, and aligned with my goal of ").append(loggedInuser.getFitnessGoal()).append(".");

        String prompt = promptBuilder.toString();
        logger.debug("Complete plan prompt length: {} characters", prompt.length());

        return askFitnessQuestion(prompt);
    }



    public void executeReportGeneration(String userId) {
        logger.info("Executing scheduled report generation: for userId {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        User loggedInuser = null;
        if (optionalUser.isPresent()) {
            loggedInuser = optionalUser.get();
        }

        try {
            // Generate fitness plan
            logger.debug("Generating fitness plan for userId : {}", userId);
            String fitnessPlan = getCompleteFitnessPlan(userId);

            String pdfPath = null;

            String fileName = generateFileName(loggedInuser.getFirstName());
            pdfPath = pdfGeneratorService.generateFitnessPlanPdf(fitnessPlan, fileName);

            String reportName = loggedInuser.getFirstName() != null ?
                    loggedInuser.getFirstName() : "Fitness Plan ";
            emailService.sendScheduledReportNotification(
                    loggedInuser.getEmail(),
                    reportName,
                    pdfPath
            );
            logger.info("Email sent to: {}", loggedInuser.getEmail());

        } catch (Exception e) {
            logger.error("Error executing scheduled report: {} ",e);
            // Optionally send error notification email
            if (loggedInuser.getEmail() != null) {
                try {
                    emailService.sendSimpleEmail(
                            loggedInuser.getEmail(),
                            "Scheduled Report Failed",
                            "Your scheduled fitness report (" + loggedInuser.getFirstName()+ ") failed to generate. " +
                                    "Error: " + e.getMessage()
                    );
                } catch (Exception emailError) {
                    logger.error("Failed to send error notification email", emailError);
                }
            }
        }
    }

    private String generateFileName(String reportName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        if (reportName != null && !reportName.isEmpty()) {
            return reportName.replaceAll("\\s+", "_") + "_" + timestamp;
        }

        return "scheduled_report_" + reportName + "_" + timestamp;
    }
    /**
     * Clear conversation history for a specific conversation ID
     */
    public void clearConversationHistory(String conversationId) {
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            conversationHistory.remove(conversationId);
            logger.info("Cleared conversation history for conversationId: {}", conversationId);
        }
    }

    /**
     * Get conversation context as a formatted string
     */
    private String getConversationContext(String conversationId) {
        List<ConversationMessage> history = conversationHistory.get(conversationId);
        if (history == null || history.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder("\n\nPrevious conversation:\n");
        for (ConversationMessage msg : history) {
            context.append(msg.role).append(": ").append(msg.content).append("\n");
        }
        return context.toString();
    }

    /**
     * Add a message to conversation history
     */
    private void addMessageToHistory(String conversationId, String role, String content) {
        conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>())
                .add(new ConversationMessage(role, content));

        // Limit history size
        List<ConversationMessage> history = conversationHistory.get(conversationId);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0); // Remove oldest message
        }
    }

    /**
     * Get all conversation IDs (for debugging/management)
     */
    public Set<String> getAllConversationIds() {
        return conversationHistory.keySet();
    }

    /**
     * Generate a complete fitness plan with structured JSON format
     */
    public String getCompleteFitnessPlanStructured(String goal, String experience, String daysPerWeek,
                                                   String targetCalories, String dietaryPreference,
                                                   String age, String gender, String currentWeight,
                                                   String targetWeight, String height) {
        logger.info("Generating STRUCTURED fitness plan - Goal: {}, Experience: {}, Days/Week: {}",
                goal, experience, daysPerWeek);

        // Build a prompt that explicitly requests clear sections with markers
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Create a COMPLETE fitness plan with CLEAR SECTION MARKERS.\n\n");

        // Personal Information
        promptBuilder.append("=== USER DETAILS ===\n");
        promptBuilder.append("Goal: ").append(goal).append("\n");
        promptBuilder.append("Experience: ").append(experience).append("\n");
        promptBuilder.append("Training Days: ").append(daysPerWeek).append(" per week\n");
        promptBuilder.append("Target Calories: ").append(targetCalories).append("\n");
        promptBuilder.append("Diet: ").append(dietaryPreference).append("\n");
        if (age != null) promptBuilder.append("Age: ").append(age).append("\n");
        if (gender != null) promptBuilder.append("Gender: ").append(gender).append("\n");
        if (currentWeight != null) promptBuilder.append("Current Weight: ").append(currentWeight).append("\n");
        if (targetWeight != null) promptBuilder.append("Target Weight: ").append(targetWeight).append("\n");
        if (height != null) promptBuilder.append("Height: ").append(height).append("\n");

        promptBuilder.append("\n\nProvide a comprehensive plan with these THREE SECTIONS clearly marked:\n\n");

        promptBuilder.append("=== SECTION 1: WORKOUT PLAN ===\n");
        promptBuilder.append("Provide:\n");
        promptBuilder.append("- Weekly training schedule for ").append(daysPerWeek).append(" days\n");
        promptBuilder.append("- Specific exercises with sets, reps, and rest periods\n");
        promptBuilder.append("- Warm-up and cool-down routines\n");
        promptBuilder.append("- Progression strategy\n\n");

        promptBuilder.append("=== SECTION 2: MEAL PLAN ===\n");
        promptBuilder.append("Provide:\n");
        promptBuilder.append("- Daily meals (Breakfast, Lunch, Dinner, Snacks) totaling ").append(targetCalories).append(" calories\n");
        promptBuilder.append("- Specific foods and portion sizes\n");
        promptBuilder.append("- Macronutrient breakdown for each meal\n");
        promptBuilder.append("- Meal timing and prep tips\n\n");

        promptBuilder.append("=== SECTION 3: SUPPLEMENT RECOMMENDATIONS ===\n");
        promptBuilder.append("Provide:\n");
        promptBuilder.append("- Essential supplements for ").append(goal).append("\n");
        promptBuilder.append("- Dosage and timing for each\n");
        promptBuilder.append("- Explanation of benefits\n\n");

        promptBuilder.append("IMPORTANT: Start each section with the exact markers shown above (=== SECTION X: NAME ===)");

        String prompt = promptBuilder.toString();
        logger.debug("Structured plan prompt length: {} characters", prompt.length());

        return askFitnessQuestion(prompt);
    }

    /**
     * Simple keyword-based validation (optional pre-check)
     */
    private boolean isLikelyFitnessRelated(String question) {
        String lowerQuestion = question.toLowerCase();
        for (String keyword : FITNESS_KEYWORDS) {
            if (lowerQuestion.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inner class to represent a conversation message
     */
    private static class ConversationMessage {
        String role;
        String content;

        ConversationMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}