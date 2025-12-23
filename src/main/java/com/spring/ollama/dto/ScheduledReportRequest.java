package com.spring.ollama.dto;

import java.time.LocalDateTime;

/**
 * Request DTO for scheduling a fitness report
 */
public class ScheduledReportRequest {

    private String scheduleType; // "ONE_TIME" or "RECURRING"
    private String email;
    private String whatsappNumber; // NEW: WhatsApp phone number
    private LocalDateTime scheduledDateTime; // For one-time schedules
    private String cronExpression; // For recurring schedules (e.g., "0 0 9 * * MON")

    // Fitness plan details
    private CompleteFitnessPlanRequest planRequest;

    // Optional
    private String reportName;
    private boolean generatePdf;
    private boolean sendEmail;
    private boolean sendWhatsApp; // NEW: Enable WhatsApp notification

    public ScheduledReportRequest() {}

    // Getters and Setters
    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public CompleteFitnessPlanRequest getPlanRequest() {
        return planRequest;
    }

    public void setPlanRequest(CompleteFitnessPlanRequest planRequest) {
        this.planRequest = planRequest;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public boolean isGeneratePdf() {
        return generatePdf;
    }

    public void setGeneratePdf(boolean generatePdf) {
        this.generatePdf = generatePdf;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isSendWhatsApp() {
        return sendWhatsApp;
    }

    public void setSendWhatsApp(boolean sendWhatsApp) {
        this.sendWhatsApp = sendWhatsApp;
    }

    @Override
    public String toString() {
        return "ScheduledReportRequest{" +
                "scheduleType='" + scheduleType + '\'' +
                ", email='" + email + '\'' +
                ", whatsappNumber='" + whatsappNumber + '\'' +
                ", scheduledDateTime=" + scheduledDateTime +
                ", cronExpression='" + cronExpression + '\'' +
                ", reportName='" + reportName + '\'' +
                ", generatePdf=" + generatePdf +
                ", sendEmail=" + sendEmail +
                ", sendWhatsApp=" + sendWhatsApp +
                '}';
    }
}

/**
 * Response DTO for scheduled report creation
 */
class ScheduledReportResponse {

    private String scheduleId;
    private String status;
    private String message;
    private LocalDateTime scheduledFor;
    private String cronExpression;
    private String email;
    private LocalDateTime createdAt;

    public ScheduledReportResponse() {}

    public ScheduledReportResponse(String scheduleId, String status, String message) {
        this.scheduleId = scheduleId;
        this.status = status;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ScheduledReportResponse{" +
                "scheduleId='" + scheduleId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", scheduledFor=" + scheduledFor +
                ", cronExpression='" + cronExpression + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}