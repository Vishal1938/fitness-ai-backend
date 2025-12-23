package com.spring.ollama.controller;

import com.spring.ollama.dto.AuthResponse;
import com.spring.ollama.dto.CompleteFitnessPlanRequest;
import com.spring.ollama.dto.RegisterRequest;
import com.spring.ollama.entity.User;
import com.spring.ollama.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")// Allow requests from frontend (adjust for production)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint: POST /api/auth/register
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        logger.info("Received registration request for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        logger.info("response {} ,{}, {}, {}",response.getMessage(),response.isSuccess(),response.getUser(),response.toString());

        if (response.isSuccess()) {
            logger.info("User registered successfully sending success response to ui {} ",response.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            logger.info("control goes into else block because of reason  {} ",response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Endpoint: POST /api/auth/login
     * Login user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody CompleteFitnessPlanRequest.LoginRequest request) {
        logger.info("Received login request for: {}", request.getEmailOrUsername());
        AuthResponse response = authService.login(request);

        if (response.isSuccess()) {
            logger.info("User logged in successfully with response {}", response.getMessage());
            return ResponseEntity.ok(response);
        } else {
            logger.info("User is unauthorised sending response to ui {} ",response.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Endpoint: GET /api/auth/user/{userId}
     * Get user details by ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        logger.info("Fetching user details for ID: {}", userId);
        Optional<User> user = authService.getUserById(userId);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Endpoint: PUT /api/auth/user/{userId}
     * Update user profile
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, @RequestBody User updatedUser) {
        logger.info("Received update request for user ID: {}", userId);
        try {
            User user = authService.updateUserProfile(userId, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            logger.error("Error updating profile", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            logger.error("Unexpected error updating profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating profile");
        }
    }
}
