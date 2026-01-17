package com.spring.ollama.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorService.class);

    @Value("${fitness.pdf.output.path:C:\\Users\\HP\\Downloads\\spring-ai-ollama\\spring-ai-ollama\\target}")
    private String pdfOutputPath;

    // Font definitions
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(33, 37, 41));
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(52, 58, 64));
    private static final Font SUBHEADING_FONT = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(73, 80, 87));
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(33, 37, 41));
    private static final Font BOLD_FONT = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(33, 37, 41));

    /**
     * Generate PDF from fitness plan text
     */
    public String generateFitnessPlanPdf(String content, String fileName) {
        logger.info("Starting PDF generation for: {}", fileName);

        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(pdfOutputPath);
            if (!Files.exists(outputDir)) {
                logger.info("path does not exist : {}",outputDir);
                Files.createDirectories(outputDir);
                logger.info("Created output directory: {}", pdfOutputPath);
            }

            // Generate filename with timestamp if not provided
            if (fileName == null || fileName.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                fileName = "fitness_plan_" + timestamp + ".pdf";
            } else if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }

            String fullPath = Paths.get(pdfOutputPath, fileName).toString();
            logger.debug("Full PDF path: {}", fullPath);

            // Create PDF document
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));

            document.open();

            // Add content to PDF
            addTitle(document, "FITNESS PLAN");
            addGenerationDate(document);
            document.add(new Paragraph("\n"));

            // Parse and format the content
            parseAndAddContent(document, content);

            document.close();

            logger.info("PDF generated successfully: {}", fullPath);
            return fullPath;

        } catch (Exception e) {
            logger.error("Error generating PDF", e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Generate PDF with structured sections
     */
    public String generateStructuredFitnessPlanPdf(String workoutPlan, String mealPlan,
                                                   String supplements, String fileName) {
        logger.info("Starting structured PDF generation for: {}", fileName);

        try {
            Path outputDir = Paths.get(pdfOutputPath);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            if (fileName == null || fileName.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                fileName = "fitness_plan_" + timestamp + ".pdf";
            } else if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }

            String fullPath = Paths.get(pdfOutputPath, fileName).toString();

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));

            document.open();

            // Add header
            addTitle(document, "COMPLETE FITNESS PLAN");
            addGenerationDate(document);
            document.add(new Paragraph("\n"));

            // Add sections
            if (workoutPlan != null && !workoutPlan.isEmpty()) {
                addSection(document, "WORKOUT PLAN", workoutPlan);
                document.add(new Paragraph("\n"));
            }

            if (mealPlan != null && !mealPlan.isEmpty()) {
                addSection(document, "MEAL PLAN", mealPlan);
                document.add(new Paragraph("\n"));
            }

            if (supplements != null && !supplements.isEmpty()) {
                addSection(document, "SUPPLEMENT RECOMMENDATIONS", supplements);
            }

            document.close();

            logger.info("Structured PDF generated successfully: {}", fullPath);
            return fullPath;

        } catch (Exception e) {
            logger.error("Error generating structured PDF", e);
            throw new RuntimeException("Failed to generate structured PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Add title to document
     */
    private void addTitle(Document document, String title) throws DocumentException {
        Paragraph titlePara = new Paragraph(title, TITLE_FONT);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(10);
        document.add(titlePara);
    }

    /**
     * Add generation date
     */
    private void addGenerationDate(Document document) throws DocumentException {
        String date = "Generated on: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        Paragraph datePara = new Paragraph(date, new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY));
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(20);
        document.add(datePara);
    }

    /**
     * Add a section with heading
     */
    private void addSection(Document document, String heading, String content) throws DocumentException {
        // Add section heading
        Paragraph headingPara = new Paragraph(heading, HEADING_FONT);
        headingPara.setSpacingBefore(10);
        headingPara.setSpacingAfter(10);
        document.add(headingPara);

        // Add horizontal line
        document.add(new Paragraph("_".repeat(100), new Font(Font.HELVETICA, 8, Font.NORMAL, Color.LIGHT_GRAY)));
        document.add(new Paragraph("\n"));

        // Add content
        parseAndAddContent(document, content);
    }

    /**
     * Parse content and add with proper formatting
     */
    private void parseAndAddContent(Document document, String content) throws DocumentException {
        if (content == null || content.isEmpty()) {
            return;
        }

        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) {
                document.add(new Paragraph("\n"));
                continue;
            }

            // Check for different line types and format accordingly
            if (line.startsWith("**") && line.endsWith("**")) {
                // Bold heading (markdown style)
                String text = line.replaceAll("\\*\\*", "");
                Paragraph para = new Paragraph(text, SUBHEADING_FONT);
                para.setSpacingBefore(8);
                para.setSpacingAfter(5);
                document.add(para);

            } else if (line.startsWith("###")) {
                // Subheading (markdown style)
                String text = line.replaceAll("###", "").trim();
                Paragraph para = new Paragraph(text, SUBHEADING_FONT);
                para.setSpacingBefore(8);
                para.setSpacingAfter(5);
                document.add(para);

            } else if (line.startsWith("##")) {
                // Heading (markdown style)
                String text = line.replaceAll("##", "").trim();
                Paragraph para = new Paragraph(text, HEADING_FONT);
                para.setSpacingBefore(10);
                para.setSpacingAfter(8);
                document.add(para);

            } else if (line.startsWith("===")) {
                // Section marker
                continue; // Skip these lines

            } else if (line.matches("^\\d+\\..*")) {
                // Numbered list
                Paragraph para = new Paragraph(line, NORMAL_FONT);
                para.setIndentationLeft(20);
                para.setSpacingAfter(3);
                document.add(para);

            } else if (line.startsWith("*") || line.startsWith("-")) {
                // Bullet point
                String text = line.substring(1).trim();
                Paragraph para = new Paragraph("• " + text, NORMAL_FONT);
                para.setIndentationLeft(20);
                para.setSpacingAfter(3);
                document.add(para);

            } else {
                // Normal text
                Paragraph para = new Paragraph(line, NORMAL_FONT);
                para.setSpacingAfter(5);
                document.add(para);
            }
        }
    }

    /**
     * Get the configured PDF output path
     */
    public String getPdfOutputPath() {
        return pdfOutputPath;
    }

    /**
     * Set custom PDF output path
     */
    public void setPdfOutputPath(String path) {
        this.pdfOutputPath = path;
    }
}