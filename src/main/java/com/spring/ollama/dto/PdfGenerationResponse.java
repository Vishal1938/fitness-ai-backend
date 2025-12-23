package com.spring.ollama.dto;

/**
 * Response DTO for PDF generation
 */
public class PdfGenerationResponse {

    private String message;
    private String pdfFilePath;
    private String fileName;
    private long fileSizeBytes;
    private String generatedAt;
    private boolean success;

    public PdfGenerationResponse() {}

    public PdfGenerationResponse(boolean success, String message, String pdfFilePath, String fileName) {
        this.success = success;
        this.message = message;
        this.pdfFilePath = pdfFilePath;
        this.fileName = fileName;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "PdfGenerationResponse{" +
                "message='" + message + '\'' +
                ", pdfFilePath='" + pdfFilePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSizeBytes=" + fileSizeBytes +
                ", generatedAt='" + generatedAt + '\'' +
                ", success=" + success +
                '}';
    }
}