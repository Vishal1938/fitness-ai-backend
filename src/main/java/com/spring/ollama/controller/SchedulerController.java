package com.spring.ollama.controller;

import com.spring.ollama.dto.ScheduledReportRequest;
import com.spring.ollama.service.ReportSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
@CrossOrigin(origins = "http://localhost:3000")
public class SchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
    private final ReportSchedulerService schedulerService;

    public SchedulerController(ReportSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
        logger.info("SchedulerController initialized");
    }

    /**
     * Schedule a report (one-time or recurring)
     */
    @PostMapping("/schedule")
    public ResponseEntity<Map<String, Object>> scheduleReport(@RequestBody ScheduledReportRequest request) {
        logger.info("Received schedule request: {}", request);

        try {
            // Validate request
            validateScheduleRequest(request);

            String scheduleId;

            if ("ONE_TIME".equalsIgnoreCase(request.getScheduleType())) {
                // Schedule one-time report
                scheduleId = schedulerService.scheduleOneTimeReport(request);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("scheduleId", scheduleId);
                response.put("message", "One-time report scheduled successfully");
                response.put("scheduledFor", request.getScheduledDateTime());
                response.put("email", request.getEmail());

                logger.info("One-time report scheduled: {}", scheduleId);
                return ResponseEntity.ok(response);

            } else if ("RECURRING".equalsIgnoreCase(request.getScheduleType())) {
                // Schedule recurring report
                scheduleId = schedulerService.scheduleRecurringReport(request);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("scheduleId", scheduleId);
                response.put("message", "Recurring report scheduled successfully");
                response.put("cronExpression", request.getCronExpression());
                response.put("email", request.getEmail());

                logger.info("Recurring report scheduled: {}", scheduleId);
                return ResponseEntity.ok(response);

            } else {
                throw new IllegalArgumentException("Invalid schedule type. Must be ONE_TIME or RECURRING");
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid schedule request: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            logger.error("Error scheduling report", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to schedule report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cancel a scheduled report
     */
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<Map<String, Object>> cancelSchedule(@PathVariable String scheduleId) {
        logger.info("Request to cancel schedule: {}", scheduleId);

        boolean cancelled = schedulerService.cancelScheduledReport(scheduleId);

        Map<String, Object> response = new HashMap<>();
        if (cancelled) {
            response.put("success", true);
            response.put("message", "Schedule cancelled successfully");
            response.put("scheduleId", scheduleId);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Schedule not found or already completed");
            response.put("scheduleId", scheduleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all active schedules
     */
    @GetMapping("/schedules")
    public ResponseEntity<Map<String, Object>> getAllSchedules() {
        logger.info("Request to get all schedules");

        Map<String, ScheduledReportRequest> schedules = schedulerService.getAllSchedules();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalSchedules", schedules.size());
        response.put("schedules", schedules);

        return ResponseEntity.ok(response);
    }

    /**
     * Get specific schedule details
     */
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<Map<String, Object>> getSchedule(@PathVariable String scheduleId) {
        logger.info("Request to get schedule: {}", scheduleId);

        ScheduledReportRequest schedule = schedulerService.getSchedule(scheduleId);

        Map<String, Object> response = new HashMap<>();
        if (schedule != null) {
            response.put("success", true);
            response.put("scheduleId", scheduleId);
            response.put("schedule", schedule);
            response.put("isActive", schedulerService.isScheduleActive(scheduleId));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Schedule not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Check if schedule is active
     */
    @GetMapping("/schedule/{scheduleId}/status")
    public ResponseEntity<Map<String, Object>> getScheduleStatus(@PathVariable String scheduleId) {
        logger.info("Request to check schedule status: {}", scheduleId);

        boolean isActive = schedulerService.isScheduleActive(scheduleId);

        Map<String, Object> response = new HashMap<>();
        response.put("scheduleId", scheduleId);
        response.put("isActive", isActive);
        response.put("status", isActive ? "ACTIVE" : "INACTIVE");

        return ResponseEntity.ok(response);
    }

    /**
     * Validate schedule request
     */
    private void validateScheduleRequest(ScheduledReportRequest request) {
        if (request.getScheduleType() == null) {
            throw new IllegalArgumentException("scheduleType is required (ONE_TIME or RECURRING)");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("email is required");
        }

        if (request.getPlanRequest() == null) {
            throw new IllegalArgumentException("planRequest is required");
        }

        if ("ONE_TIME".equalsIgnoreCase(request.getScheduleType())) {
            if (request.getScheduledDateTime() == null) {
                throw new IllegalArgumentException("scheduledDateTime is required for ONE_TIME schedules");
            }
        } else if ("RECURRING".equalsIgnoreCase(request.getScheduleType())) {
            if (request.getCronExpression() == null || request.getCronExpression().trim().isEmpty()) {
                throw new IllegalArgumentException("cronExpression is required for RECURRING schedules");
            }
        } else {
            throw new IllegalArgumentException("scheduleType must be ONE_TIME or RECURRING");
        }
    }
}