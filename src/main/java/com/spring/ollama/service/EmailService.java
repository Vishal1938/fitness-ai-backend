package com.spring.ollama.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        logger.info("EmailService initialized");
    }

    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        logger.info("Sending simple email to: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send email with PDF attachment
     */
    public void sendEmailWithAttachment(String toEmail, String subject, String body, String pdfFilePath) {
        logger.info("Sending email with attachment to: {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            // Attach PDF if provided
            if (pdfFilePath != null && !pdfFilePath.isEmpty()) {
                File file = new File(pdfFilePath);
                if (file.exists()) {
                    FileSystemResource fileResource = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), fileResource);
                    logger.debug("Attached PDF: {}", file.getName());
                } else {
                    logger.warn("PDF file not found: {}", pdfFilePath);
                }
            }

            mailSender.send(message);
            logger.info("Email with attachment sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send email with attachment to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email with attachment: " + e.getMessage(), e);
        }
    }

    /**
     * Send fitness plan email with PDF
     */
    public void sendFitnessPlanEmail(String toEmail, String planContent, String pdfFilePath) {
        logger.info("Sending fitness plan email to: {}", toEmail);

        String subject = "Your Personalized Fitness Plan";
        String body = buildFitnessPlanEmailBody(planContent, pdfFilePath);

        if (pdfFilePath != null && !pdfFilePath.isEmpty()) {
            sendEmailWithAttachment(toEmail, subject, body, pdfFilePath);
        } else {
            sendSimpleEmail(toEmail, subject, body);
        }
    }

    /**
     * Build HTML email body for fitness plan
     */
    private String buildFitnessPlanEmailBody(String planContent, String pdfFilePath) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append("h1 { color: #2c3e50; }");
        html.append("h2 { color: #34495e; }");
        html.append(".container { max-width: 800px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #3498db; color: white; padding: 20px; text-align: center; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; }");
        html.append(".footer { margin-top: 30px; padding: 20px; background-color: #ecf0f1; text-align: center; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        html.append("<div class='header'>");
        html.append("<h1>🏋️ Your Personalized Fitness Plan</h1>");
        html.append("</div>");

        html.append("<div class='content'>");
        html.append("<h2>Hello!</h2>");
        html.append("<p>Your personalized fitness plan has been generated and is ready for you.</p>");

        if (pdfFilePath != null && !pdfFilePath.isEmpty()) {
            html.append("<p><strong>📎 Your fitness plan is attached as a PDF document.</strong></p>");
        }

        html.append("<p>This comprehensive plan includes:</p>");
        html.append("<ul>");
        html.append("<li>✅ Customized workout schedule</li>");
        html.append("<li>✅ Detailed meal plan with macros</li>");
        html.append("<li>✅ Supplement recommendations</li>");
        html.append("</ul>");

        html.append("<p><em>Remember: Consistency is key! Stay committed to your plan and track your progress regularly.</em></p>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>💪 Good luck on your fitness journey!</p>");
        html.append("<p style='font-size: 12px; color: #7f8c8d;'>Generated by Fitness AI | Powered by Spring AI + Ollama</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Send scheduled report notification
     */
    public void sendScheduledReportNotification(String toEmail, String reportName, String pdfPath) {
        logger.info("Sending scheduled report notification to: {}", toEmail);

        String subject = "Scheduled Fitness Report: " + reportName;
        String body = buildScheduledReportEmailBody(reportName);

        sendEmailWithAttachment(toEmail, subject, body, pdfPath);
    }

    /**
     * Build HTML email body for scheduled report
     */
    private String buildScheduledReportEmailBody(String reportName) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #27ae60; color: white; padding: 20px; text-align: center; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        html.append("<div class='header'>");
        html.append("<h1>📊 Scheduled Fitness Report</h1>");
        html.append("</div>");

        html.append("<div class='content'>");
        html.append("<h2>Your Report is Ready!</h2>");
        html.append("<p>Report Name: <strong>").append(reportName).append("</strong></p>");
        html.append("<p>This is your automated fitness report as per your schedule.</p>");
        html.append("<p>The detailed report is attached as a PDF.</p>");
        html.append("<p style='margin-top: 20px;'><em>Stay consistent and keep tracking your progress!</em></p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
}