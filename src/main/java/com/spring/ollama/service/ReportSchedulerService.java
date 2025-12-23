package com.spring.ollama.service;

import com.spring.ollama.dto.CompleteFitnessPlanRequest;
import com.spring.ollama.dto.ScheduledReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class ReportSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(ReportSchedulerService.class);

    private final TaskScheduler taskScheduler;
    private final FitnessAiService fitnessAiService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    // Store scheduled tasks
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledReportRequest> scheduledReports = new ConcurrentHashMap<>();

    public ReportSchedulerService(TaskScheduler taskScheduler,
                                  FitnessAiService fitnessAiService,
                                  PdfGeneratorService pdfGeneratorService,
                                  EmailService emailService,
                                  WhatsAppService whatsAppService) {
        this.taskScheduler = taskScheduler;
        this.fitnessAiService = fitnessAiService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.emailService = emailService;
        this.whatsAppService = whatsAppService;
        logger.info("ReportSchedulerService initialized");
    }

    /**
     * Schedule a one-time report
     */
    public String scheduleOneTimeReport(ScheduledReportRequest request) {
        String scheduleId = UUID.randomUUID().toString();
        logger.info("Scheduling one-time report: {} for {}", scheduleId, request.getScheduledDateTime());

        try {
            // Convert LocalDateTime to Instant
            Instant scheduledInstant = request.getScheduledDateTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            // Create the task
            Runnable task = () -> executeReportGeneration(scheduleId, request);

            // Schedule the task
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, scheduledInstant);

            // Store the task and request
            scheduledTasks.put(scheduleId, scheduledTask);
            scheduledReports.put(scheduleId, request);

            logger.info("One-time report scheduled successfully: {}", scheduleId);
            return scheduleId;

        } catch (Exception e) {
            logger.error("Failed to schedule one-time report", e);
            throw new RuntimeException("Failed to schedule report: " + e.getMessage(), e);
        }
    }

    /**
     * Schedule a recurring report using cron expression
     */
    public String scheduleRecurringReport(ScheduledReportRequest request) {
        String scheduleId = UUID.randomUUID().toString();
        logger.info("Scheduling recurring report: {} with cron: {}", scheduleId, request.getCronExpression());

        try {
            // Validate cron expression
            CronTrigger cronTrigger = new CronTrigger(request.getCronExpression());

            // Create the task
            Runnable task = () -> executeReportGeneration(scheduleId, request);

            // Schedule the task
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, cronTrigger);

            // Store the task and request
            scheduledTasks.put(scheduleId, scheduledTask);
            scheduledReports.put(scheduleId, request);

            logger.info("Recurring report scheduled successfully: {}", scheduleId);
            return scheduleId;

        } catch (Exception e) {
            logger.error("Failed to schedule recurring report", e);
            throw new RuntimeException("Failed to schedule recurring report: " + e.getMessage(), e);
        }
    }

    /**
     * Execute report generation task
     */
    private void executeReportGeneration(String scheduleId, ScheduledReportRequest request) {
        logger.info("Executing scheduled report generation: {}", scheduleId);

        try {
            CompleteFitnessPlanRequest planRequest = request.getPlanRequest();

            // Generate fitness plan
            logger.debug("Generating fitness plan for schedule: {}", scheduleId);
            String fitnessPlan = fitnessAiService.getCompleteFitnessPlan(
                    planRequest.getGoal(),
                    planRequest.getExperience(),
                    planRequest.getDaysPerWeek(),
                    planRequest.getTargetCalories(),
                    planRequest.getDietaryPreference(),
                    planRequest.getAge(),
                    planRequest.getGender(),
                    planRequest.getCurrentWeight(),
                    planRequest.getTargetWeight(),
                    planRequest.getHeight(),
                    planRequest.getAditionalInfo()
            );

            String pdfPath = null;

            // Generate PDF if requested
            if (request.isGeneratePdf()) {
                logger.debug("Generating PDF for schedule: {}", scheduleId);
                String fileName = generateFileName(request.getReportName(), scheduleId);
                pdfPath = pdfGeneratorService.generateFitnessPlanPdf(fitnessPlan, fileName);
                logger.info("PDF generated: {}", pdfPath);
            }

            // Send email if requested
            if (request.isSendEmail() && request.getEmail() != null) {
                logger.debug("Sending email for schedule: {}", scheduleId);
                String reportName = request.getReportName() != null ?
                        request.getReportName() : "Fitness Plan " + scheduleId;
                emailService.sendScheduledReportNotification(
                        request.getEmail(),
                        reportName,
                        pdfPath
                );
                logger.info("Email sent to: {}", request.getEmail());
            }

            // Send WhatsApp message if requested
            if (request.isSendWhatsApp() && request.getWhatsappNumber() != null) {
                logger.debug("Sending WhatsApp message for schedule: {}", scheduleId);
                String reportName = request.getReportName() != null ?
                        request.getReportName() : "Fitness Plan " + scheduleId;

                boolean whatsappSent = whatsAppService.sendScheduledReportNotification(
                        request.getWhatsappNumber(),
                        reportName,
                        pdfPath
                );

                if (whatsappSent) {
                    logger.info("WhatsApp message sent to: {}", request.getWhatsappNumber());
                } else {
                    logger.warn("Failed to send WhatsApp message to: {}", request.getWhatsappNumber());
                }
            }

            logger.info("Scheduled report executed successfully: {}", scheduleId);

            // Remove one-time schedules after execution
            if ("ONE_TIME".equals(request.getScheduleType())) {
                scheduledTasks.remove(scheduleId);
                scheduledReports.remove(scheduleId);
                logger.info("One-time schedule removed: {}", scheduleId);
            }

        } catch (Exception e) {
            logger.error("Error executing scheduled report: {}", scheduleId, e);
            // Optionally send error notification email
            if (request.getEmail() != null) {
                try {
                    emailService.sendSimpleEmail(
                            request.getEmail(),
                            "Scheduled Report Failed",
                            "Your scheduled fitness report (" + scheduleId + ") failed to generate. " +
                                    "Error: " + e.getMessage()
                    );
                } catch (Exception emailError) {
                    logger.error("Failed to send error notification email", emailError);
                }
            }
        }
    }

    /**
     * Cancel a scheduled report
     */
    public boolean cancelScheduledReport(String scheduleId) {
        logger.info("Cancelling scheduled report: {}", scheduleId);

        ScheduledFuture<?> task = scheduledTasks.get(scheduleId);
        if (task != null) {
            boolean cancelled = task.cancel(false);
            if (cancelled) {
                scheduledTasks.remove(scheduleId);
                scheduledReports.remove(scheduleId);
                logger.info("Scheduled report cancelled: {}", scheduleId);
                return true;
            }
        }

        logger.warn("Scheduled report not found or already completed: {}", scheduleId);
        return false;
    }

    /**
     * Get all active schedules
     */
    public Map<String, ScheduledReportRequest> getAllSchedules() {
        return new ConcurrentHashMap<>(scheduledReports);
    }

    /**
     * Get specific schedule details
     */
    public ScheduledReportRequest getSchedule(String scheduleId) {
        return scheduledReports.get(scheduleId);
    }

    /**
     * Check if schedule exists and is active
     */
    public boolean isScheduleActive(String scheduleId) {
        ScheduledFuture<?> task = scheduledTasks.get(scheduleId);
        return task != null && !task.isDone() && !task.isCancelled();
    }

    /**
     * Generate filename for scheduled report
     */
    private String generateFileName(String reportName, String scheduleId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        if (reportName != null && !reportName.isEmpty()) {
            return reportName.replaceAll("\\s+", "_") + "_" + timestamp;
        }

        return "scheduled_report_" + scheduleId.substring(0, 8) + "_" + timestamp;
    }
}