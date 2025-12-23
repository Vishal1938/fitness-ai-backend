package com.spring.ollama.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    @Value("${whatsapp.api.url:https://graph.facebook.com/v18.0}")
    private String whatsappApiUrl;

    @Value("${whatsapp.phone.number.id:}")
    private String phoneNumberId;

    @Value("${whatsapp.access.token:}")
    private String accessToken;

    @Value("${whatsapp.enabled:false}")
    private boolean whatsappEnabled;

    private final RestTemplate restTemplate;

    public WhatsAppService() {
        this.restTemplate = new RestTemplate();
        logger.info("WhatsAppService initialized");
    }

    /**
     * Check if WhatsApp is enabled and configured
     */
    public boolean isWhatsAppEnabled() {
        if (!whatsappEnabled) {
            logger.debug("WhatsApp is disabled in configuration");
            return false;
        }

        if (phoneNumberId == null || phoneNumberId.isEmpty() ||
                accessToken == null || accessToken.isEmpty()) {
            logger.warn("WhatsApp credentials not configured");
            return false;
        }

        return true;
    }

    /**
     * Send text message via WhatsApp
     */
    public boolean sendTextMessage(String recipientPhone, String message) {
        if (!isWhatsAppEnabled()) {
            logger.warn("WhatsApp is not enabled or configured");
            return false;
        }

        logger.info("Sending WhatsApp text message to: {}", recipientPhone);

        try {
            String url = String.format("%s/%s/messages", whatsappApiUrl, phoneNumberId);

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messaging_product", "whatsapp");
            requestBody.put("recipient_type", "individual");
            requestBody.put("to", formatPhoneNumber(recipientPhone));
            requestBody.put("type", "text");

            Map<String, String> text = new HashMap<>();
            text.put("body", message);
            requestBody.put("text", text);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Send request
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("WhatsApp text message sent successfully to: {}", recipientPhone);
                return true;
            } else {
                logger.error("Failed to send WhatsApp message. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending WhatsApp text message to: {}", recipientPhone, e);
            return false;
        }
    }

    /**
     * Send document (PDF) via WhatsApp
     */
    public boolean sendDocument(String recipientPhone, String pdfFilePath, String caption) {
        if (!isWhatsAppEnabled()) {
            logger.warn("WhatsApp is not enabled or configured");
            return false;
        }

        logger.info("Sending WhatsApp document to: {}", recipientPhone);

        try {
            // Step 1: Upload the media file
            String mediaId = uploadMedia(pdfFilePath);
            if (mediaId == null) {
                logger.error("Failed to upload media file");
                return false;
            }

            // Step 2: Send the document message
            String url = String.format("%s/%s/messages", whatsappApiUrl, phoneNumberId);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messaging_product", "whatsapp");
            requestBody.put("recipient_type", "individual");
            requestBody.put("to", formatPhoneNumber(recipientPhone));
            requestBody.put("type", "document");

            Map<String, Object> document = new HashMap<>();
            document.put("id", mediaId);
            if (caption != null && !caption.isEmpty()) {
                document.put("caption", caption);
            }
            document.put("filename", new File(pdfFilePath).getName());
            requestBody.put("document", document);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("WhatsApp document sent successfully to: {}", recipientPhone);
                return true;
            } else {
                logger.error("Failed to send WhatsApp document. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending WhatsApp document to: {}", recipientPhone, e);
            return false;
        }
    }

    /**
     * Upload media file to WhatsApp servers
     */
    private String uploadMedia(String filePath) {
        logger.debug("Uploading media file: {}", filePath);

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("File not found: {}", filePath);
                return null;
            }

            String url = String.format("%s/%s/media", whatsappApiUrl, phoneNumberId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(accessToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("messaging_product", "whatsapp");
            body.add("file", new FileSystemResource(file));
            body.add("type", "application/pdf");

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String mediaId = (String) response.getBody().get("id");
                logger.info("Media uploaded successfully. Media ID: {}", mediaId);
                return mediaId;
            } else {
                logger.error("Failed to upload media. Status: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("Error uploading media file", e);
            return null;
        }
    }

    /**
     * Send fitness plan notification via WhatsApp
     */
    public boolean sendFitnessPlanNotification(String recipientPhone, String reportName, String pdfPath) {
        logger.info("Sending fitness plan notification via WhatsApp to: {}", recipientPhone);

        try {
            // Send initial text message
            String message = String.format(
                    "🏋️ *Your Fitness Plan is Ready!*%n%n" +
                            "Report: *%s*%n%n" +
                            "Your personalized fitness plan has been generated and includes:%n" +
                            "✅ Workout schedule%n" +
                            "✅ Meal plan%n" +
                            "✅ Supplement recommendations%n%n" +
                            "Sending your PDF now... 📄",
                    reportName
            );

            boolean textSent = sendTextMessage(recipientPhone, message);
            if (!textSent) {
                logger.warn("Failed to send initial WhatsApp text message");
            }

            // Send PDF document
            String caption = String.format("📊 %s - Fitness Plan Report", reportName);
            boolean docSent = sendDocument(recipientPhone, pdfPath, caption);

            if (docSent) {
                // Send follow-up message
                String followUp = "💪 Good luck on your fitness journey! Stay consistent and track your progress.";
                sendTextMessage(recipientPhone, followUp);
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("Error sending fitness plan notification via WhatsApp", e);
            return false;
        }
    }

    /**
     * Send scheduled report notification via WhatsApp
     */
    public boolean sendScheduledReportNotification(String recipientPhone, String reportName, String pdfPath) {
        logger.info("Sending scheduled report notification via WhatsApp to: {}", recipientPhone);

        String message = String.format(
                "📊 *Scheduled Fitness Report*%n%n" +
                        "Report: *%s*%n%n" +
                        "Your automated fitness report is ready as per your schedule.%n%n" +
                        "Sending PDF now... 📄",
                reportName
        );

        sendTextMessage(recipientPhone, message);

        String caption = String.format("📅 Scheduled Report: %s", reportName);
        return sendDocument(recipientPhone, pdfPath, caption);
    }

    /**
     * Format phone number for WhatsApp API (remove spaces, dashes, etc.)
     * Expected format: Country code + phone number (e.g., 919876543210)
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        // Remove all non-digit characters
        String formatted = phoneNumber.replaceAll("[^0-9]", "");

        // If doesn't start with country code, assume India (+91)
        if (!formatted.startsWith("91") && formatted.length() == 10) {
            formatted = "91" + formatted;
        }

        logger.debug("Formatted phone number: {} -> {}", phoneNumber, formatted);
        return formatted;
    }

    /**
     * Send template message (for pre-approved WhatsApp Business templates)
     */
    public boolean sendTemplateMessage(String recipientPhone, String templateName, Map<String, String> parameters) {
        if (!isWhatsAppEnabled()) {
            logger.warn("WhatsApp is not enabled or configured");
            return false;
        }

        logger.info("Sending WhatsApp template message to: {}", recipientPhone);

        try {
            String url = String.format("%s/%s/messages", whatsappApiUrl, phoneNumberId);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messaging_product", "whatsapp");
            requestBody.put("to", formatPhoneNumber(recipientPhone));
            requestBody.put("type", "template");

            Map<String, Object> template = new HashMap<>();
            template.put("name", templateName);
            template.put("language", Map.of("code", "en"));

            // Add parameters if provided
            if (parameters != null && !parameters.isEmpty()) {
                // Build components array with parameters
                // This is a simplified version - adjust based on your template structure
            }

            requestBody.put("template", template);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            logger.error("Error sending WhatsApp template message", e);
            return false;
        }
    }
}