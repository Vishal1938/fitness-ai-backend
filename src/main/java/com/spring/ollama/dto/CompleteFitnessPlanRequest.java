package com.spring.ollama.dto;

/**
 * Request DTO for generating a complete fitness plan including:
 * - Workout Plan
 * - Meal Plan
 * - Supplement Recommendations
 */
public class CompleteFitnessPlanRequest {

    // Common fields
    private String goal;                    // e.g., "muscle gain", "fat loss", "strength", "endurance"
    private String experience;              // e.g., "beginner", "intermediate", "advanced"

    // Workout-specific fields
    private String daysPerWeek;             // e.g., "3", "4", "5", "6"

    // Nutrition-specific fields
    private String targetCalories;          // e.g., "2000", "2500", "3000"
    private String dietaryPreference;       // e.g., "vegetarian", "vegan", "keto", "balanced", "paleo"

    // Personal details (optional but helpful)
    private String age;                     // e.g., "25", "30", "40"
    private String gender;                  // e.g., "male", "female", "other"
    private String currentWeight;           // e.g., "70kg", "180lbs"
    private String targetWeight;            // e.g., "75kg", "165lbs"
    private String height;                  // e.g., "175cm", "5'10"

    private String aditionalInfo;

    public String getAditionalInfo() {
        return aditionalInfo;
    }

    public void setAditionalInfo(String aditionalInfo) {
        this.aditionalInfo = aditionalInfo;
    }

    // Default constructor
    public CompleteFitnessPlanRequest() {}

    // Constructor with essential fields
    public CompleteFitnessPlanRequest(String goal, String experience, String daysPerWeek,
                                      String targetCalories, String dietaryPreference) {
        this.goal = goal;
        this.experience = experience;
        this.daysPerWeek = daysPerWeek;
        this.targetCalories = targetCalories;
        this.dietaryPreference = dietaryPreference;
    }

    // Getters and Setters
    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(String daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public String getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(String targetCalories) {
        this.targetCalories = targetCalories;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(String currentWeight) {
        this.currentWeight = currentWeight;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "CompleteFitnessPlanRequest{" +
                "goal='" + goal + '\'' +
                ", experience='" + experience + '\'' +
                ", daysPerWeek='" + daysPerWeek + '\'' +
                ", targetCalories='" + targetCalories + '\'' +
                ", dietaryPreference='" + dietaryPreference + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", currentWeight='" + currentWeight + '\'' +
                ", targetWeight='" + targetWeight + '\'' +
                ", height='" + height + '\'' +
                '}';
    }

    // ==================== LoginRequest ====================
    public static class LoginRequest {
        private String emailOrUsername;
        private String password;

        public LoginRequest() {}

        public LoginRequest(String emailOrUsername, String password) {
            this.emailOrUsername = emailOrUsername;
            this.password = password;
        }

        public String getEmailOrUsername() {
            return emailOrUsername;
        }

        public void setEmailOrUsername(String emailOrUsername) {
            this.emailOrUsername = emailOrUsername;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // ==================== UserDTO ====================
    static class UserDTO {
        private String id;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String role;
        private Integer age;
        private String gender;
        private String currentWeight;
        private String targetWeight;
        private String height;
        private String fitnessGoal;
        private String experienceLevel;

        public UserDTO() {}

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getCurrentWeight() {
            return currentWeight;
        }

        public void setCurrentWeight(String currentWeight) {
            this.currentWeight = currentWeight;
        }

        public String getTargetWeight() {
            return targetWeight;
        }

        public void setTargetWeight(String targetWeight) {
            this.targetWeight = targetWeight;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getFitnessGoal() {
            return fitnessGoal;
        }

        public void setFitnessGoal(String fitnessGoal) {
            this.fitnessGoal = fitnessGoal;
        }

        public String getExperienceLevel() {
            return experienceLevel;
        }

        public void setExperienceLevel(String experienceLevel) {
            this.experienceLevel = experienceLevel;
        }
    }
}