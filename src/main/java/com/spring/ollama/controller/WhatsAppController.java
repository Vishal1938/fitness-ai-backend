package com.spring.ollama.controller;

import com.spring.ollama.service.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = "http://localhost:3000")
public class WhatsAppController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppController.class);
    private final WhatsAppService whatsAppService;

    public WhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
        logger.info("WhatsAppController initialized");
    }

    /**
     * Check if WhatsApp is enabled and configured
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWhatsAppStatus() {
        logger.info("Checking WhatsApp status");

        Map<String, Object> response = new HashMap<>();
        boolean enabled = whatsAppService.isWhatsAppEnabled();

        response.put("enabled", enabled);
        response.put("status", enabled ? "CONFIGURED" : "NOT_CONFIGURED");
        response.put("message", enabled ?
                "WhatsApp is configured and ready" :
                "WhatsApp is not configured. Check application.properties");

        return ResponseEntity.ok(response);
    }

    /**
     * Send test text message
     */
    @PostMapping("/test-message")
    public ResponseEntity<Map<String, Object>> sendTestMessage(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String message) {

        logger.info("Sending test WhatsApp message to: {}", phoneNumber);

        Map<String, Object> response = new HashMap<>();

        if (!whatsAppService.isWhatsAppEnabled()) {
            response.put("success", false);
            response.put("error", "WhatsApp is not enabled or configured");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        try {
            String testMessage = message != null ? message :
                    "🏋️ Hello from Fitness AI! This is a test message from your fitness report scheduler.";

            boolean sent = whatsAppService.sendTextMessage(phoneNumber, testMessage);

            response.put("success", sent);
            response.put("phoneNumber", phoneNumber);
            response.put("message", sent ? "Message sent successfully" : "Failed to send message");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sending test message", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Send test document (PDF)
     */
    @PostMapping("/test-document")
    public ResponseEntity<Map<String, Object>> sendTestDocument(
            @RequestParam String phoneNumber,
            @RequestParam String pdfPath,
            @RequestParam(required = false) String caption) {

        logger.info("Sending test WhatsApp document to: {}", phoneNumber);

        Map<String, Object> response = new HashMap<>();

        if (!whatsAppService.isWhatsAppEnabled()) {
            response.put("success", false);
            response.put("error", "WhatsApp is not enabled or configured");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        try {
            String testCaption = caption != null ? caption : "📊 Test Fitness Report";
            boolean sent = whatsAppService.sendDocument(phoneNumber, pdfPath, testCaption);

            response.put("success", sent);
            response.put("phoneNumber", phoneNumber);
            response.put("pdfPath", pdfPath);
            response.put("message", sent ? "Document sent successfully" : "Failed to send document");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sending test document", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Send fitness plan notification to WhatsApp
     */
    @PostMapping("/send-fitness-plan")
    public ResponseEntity<Map<String, Object>> sendFitnessPlan(
            @RequestParam String phoneNumber,
            @RequestParam String reportName,
            @RequestParam String pdfPath) {

        logger.info("Sending fitness plan notification to WhatsApp: {}", phoneNumber);

        Map<String, Object> response = new HashMap<>();

        if (!whatsAppService.isWhatsAppEnabled()) {
            response.put("success", false);
            response.put("error", "WhatsApp is not enabled or configured");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        try {
            boolean sent = whatsAppService.sendFitnessPlanNotification(phoneNumber, reportName, pdfPath);

            response.put("success", sent);
            response.put("phoneNumber", phoneNumber);
            response.put("reportName", reportName);
            response.put("message", sent ? "Fitness plan sent successfully" : "Failed to send fitness plan");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sending fitness plan", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}