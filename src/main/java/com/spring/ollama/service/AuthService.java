package com.spring.ollama.service;

import com.spring.ollama.dto.*;
import com.spring.ollama.entity.User;
import com.spring.ollama.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("AuthService initialized");
    }

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user: {}", request.getEmail());

        try {
            // Validate input
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new AuthResponse(false, "Email is required");
            }
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return new AuthResponse(false, "Username is required");
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return new AuthResponse(false, "Password must be at least 6 characters");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Email already exists: {}", request.getEmail());
                return new AuthResponse(false, "Email already registered");
            }

            // Check if username already exists
            if (userRepository.existsByUsername(request.getUsername())) {
                logger.warn("Username already exists: {}", request.getUsername());
                return new AuthResponse(false, "Username already taken");
            }

            // Create new user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setEnabled(true);
            user.setRole("USER");

            // Save user
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully: {}", savedUser.getId());

            // Generate token (simple UUID for now)
            String token = UUID.randomUUID().toString();

            // Create UserDTO
            UserDTO userDTO = convertToDTO(savedUser);

            return new AuthResponse(true, "Registration successful", token, userDTO);

        } catch (Exception e) {
            logger.error("Error during registration", e);
            return new AuthResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    /**
     * Login user
     */
    public AuthResponse login(CompleteFitnessPlanRequest.LoginRequest request) {
        logger.info("Login attempt for: {}", request.getEmailOrUsername());

        try {
            // Find user by email or username
            Optional<User> userOpt = userRepository.findByEmail(request.getEmailOrUsername());
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByUsername(request.getEmailOrUsername());
            }

            if (userOpt.isEmpty()) {
                logger.warn("User not found: {}", request.getEmailOrUsername());
                return new AuthResponse(false, "Invalid credentials");
            }

            User user = userOpt.get();

            // Check if account is enabled
            if (!user.isEnabled()) {
                logger.warn("Account disabled: {}", user.getEmail());
                return new AuthResponse(false, "Account is disabled");
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Invalid password for user: {}", user.getEmail());
                return new AuthResponse(false, "Invalid credentials");
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate token
            String token = UUID.randomUUID().toString();

            // Create UserDTO
            UserDTO userDTO = convertToDTO(user);

            logger.info("Login successful for user: {}", user.getId());
            return new AuthResponse(true, "Login successful", token, userDTO);

        } catch (Exception e) {
            logger.error("Error during login", e);
            return new AuthResponse(false, "Login failed: " + e.getMessage());
        }
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(String userId, User updatedUser) {
        logger.info("Updating profile for user: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Update fields
        if (updatedUser.getFirstName() != null) user.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) user.setLastName(updatedUser.getLastName());
        if (updatedUser.getPhoneNumber() != null) user.setPhoneNumber(updatedUser.getPhoneNumber());
        if (updatedUser.getAge() != null) user.setAge(updatedUser.getAge());
        if (updatedUser.getGender() != null) user.setGender(updatedUser.getGender());
        if (updatedUser.getCurrentWeight() != null) user.setCurrentWeight(updatedUser.getCurrentWeight());
        if (updatedUser.getTargetWeight() != null) user.setTargetWeight(updatedUser.getTargetWeight());
        if (updatedUser.getHeight() != null) user.setHeight(updatedUser.getHeight());
        if (updatedUser.getFitnessGoal() != null) user.setFitnessGoal(updatedUser.getFitnessGoal());
        if (updatedUser.getExperienceLevel() != null) user.setExperienceLevel(updatedUser.getExperienceLevel());
        if(updatedUser.getDietaryPreference() !=null)user.setDietaryPreference(updatedUser.getDietaryPreference());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setCurrentWeight(user.getCurrentWeight());
        dto.setTargetWeight(user.getTargetWeight());
        dto.setHeight(user.getHeight());
        dto.setFitnessGoal(user.getFitnessGoal());
        dto.setExperienceLevel(user.getExperienceLevel());
        return dto;
    }
}